package com.pokestock.ms_usuarios.service.impl;

import com.pokestock.ms_usuarios.dto.request.UsuarioRequestDTO;
import com.pokestock.ms_usuarios.dto.response.RolResponseDTO;
import com.pokestock.ms_usuarios.dto.response.UsuarioResponseDTO;
import com.pokestock.ms_usuarios.model.Rol;
import com.pokestock.ms_usuarios.model.Usuario;
import com.pokestock.ms_usuarios.repository.RolRepository;
import com.pokestock.ms_usuarios.repository.UsuarioRepository;
import com.pokestock.ms_usuarios.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO obtenerPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        return toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        log.info("Creando nuevo usuario: {}", dto.getUsername());
        
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("El nombre de usuario ya está registrado: " + dto.getUsername());
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("El correo electrónico ya está registrado: " + dto.getEmail());
        }

        Set<Rol> roles = new HashSet<>();
        if (dto.getRolIds() != null && !dto.getRolIds().isEmpty()) {
            roles = dto.getRolIds().stream()
                    .map(rolId -> rolRepository.findById(rolId)
                            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + rolId)))
                    .collect(Collectors.toSet());
        }

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(dto.getPassword()) // En producción, encriptar contraseña
                .email(dto.getEmail())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .roles(roles)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

        if (!usuario.getUsername().equals(dto.getUsername()) && usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("El nombre de usuario ya está registrado: " + dto.getUsername());
        }

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("El correo electrónico ya está registrado: " + dto.getEmail());
        }

        Set<Rol> roles = new HashSet<>();
        if (dto.getRolIds() != null) {
            roles = dto.getRolIds().stream()
                    .map(rolId -> rolRepository.findById(rolId)
                            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + rolId)))
                    .collect(Collectors.toSet());
        }

        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        usuario.setEmail(dto.getEmail());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        if (dto.getRolIds() != null) {
            usuario.setRoles(roles);
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id) {
        log.info("Desactivando lógicamente usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO asignarRoles(Long id, Set<Long> rolIds) {
        log.info("Asignando roles al usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

        Set<Rol> roles = rolIds.stream()
                .map(rolId -> rolRepository.findById(rolId)
                        .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + rolId)))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);
        Usuario guardado = usuarioRepository.save(usuario);
        return toResponseDTO(guardado);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        Set<RolResponseDTO> rolesDTO = usuario.getRoles().stream()
                .map(rol -> RolResponseDTO.builder()
                        .id(rol.getId())
                        .nombre(rol.getNombre())
                        .descripcion(rol.getDescripcion())
                        .build())
                .collect(Collectors.toSet());

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .activo(usuario.getActivo())
                .roles(rolesDTO)
                .build();
    }

    @Override
    public com.pokestock.ms_usuarios.dto.response.UsuarioInternalDTO obtenerInternoPorUsername(String username) {
        log.info("Buscando usuario interno con username: {}", username);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con username: " + username));
        
        Set<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.toSet());
                
        return com.pokestock.ms_usuarios.dto.response.UsuarioInternalDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .activo(usuario.getActivo())
                .roles(roles)
                .build();
    }
}
