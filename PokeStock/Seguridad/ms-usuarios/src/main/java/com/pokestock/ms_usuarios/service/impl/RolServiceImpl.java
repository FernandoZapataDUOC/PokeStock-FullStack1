package com.pokestock.ms_usuarios.service.impl;

import com.pokestock.ms_usuarios.dto.request.RolRequestDTO;
import com.pokestock.ms_usuarios.dto.response.RolResponseDTO;
import com.pokestock.ms_usuarios.model.Rol;
import com.pokestock.ms_usuarios.repository.RolRepository;
import com.pokestock.ms_usuarios.service.RolService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    public List<RolResponseDTO> listarTodos() {
        log.info("Listando todos los roles");
        return rolRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public RolResponseDTO obtenerPorId(Long id) {
        log.info("Buscando rol con id: {}", id);
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));
        return toResponseDTO(rol);
    }

    @Override
    @Transactional
    public RolResponseDTO crearRol(RolRequestDTO dto) {
        log.info("Creando nuevo rol: {}", dto.getNombre());
        if (rolRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalStateException("Ya existe un rol con el nombre: " + dto.getNombre());
        }
        Rol rol = Rol.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
        Rol guardado = rolRepository.save(rol);
        return toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public void eliminarRol(Long id) {
        log.info("Eliminando rol con id: {}", id);
        if (!rolRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado con id: " + id);
        }
        rolRepository.deleteById(id);
    }

    private RolResponseDTO toResponseDTO(Rol rol) {
        return RolResponseDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .build();
    }
}
