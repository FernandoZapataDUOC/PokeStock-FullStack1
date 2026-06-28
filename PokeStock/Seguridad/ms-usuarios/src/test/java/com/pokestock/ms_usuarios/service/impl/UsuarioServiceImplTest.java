package com.pokestock.ms_usuarios.service.impl;

import com.pokestock.ms_usuarios.dto.request.UsuarioRequestDTO;
import com.pokestock.ms_usuarios.dto.response.UsuarioResponseDTO;
import com.pokestock.ms_usuarios.model.Rol;
import com.pokestock.ms_usuarios.model.Usuario;
import com.pokestock.ms_usuarios.repository.RolRepository;
import com.pokestock.ms_usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - UsuarioServiceImpl")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioActivo;
    private Rol rolUser;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        rolUser = Rol.builder()
                .id(1L)
                .nombre("ROLE_USER")
                .descripcion("Rol de usuario")
                .build();

        usuarioActivo = Usuario.builder()
                .id(1L)
                .username("ash.ketchum")
                .password("pikachu123")
                .email("ash@kanto.com")
                .nombre("Ash")
                .apellido("Ketchum")
                .activo(true)
                .roles(Set.of(rolUser))
                .build();

        requestDTO = UsuarioRequestDTO.builder()
                .username("ash.ketchum")
                .password("pikachu123")
                .email("ash@kanto.com")
                .nombre("Ash")
                .apellido("Ketchum")
                .rolIds(Set.of(1L))
                .build();
    }

    // ==============================
    // listarTodos()
    // ==============================

    @Test
    @DisplayName("listarTodos - debe retornar todos los usuarios")
    void listarTodos_debeRetornarTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioActivo));

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsername()).isEqualTo("ash.ketchum");
        verify(usuarioRepository).findAll();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar usuario cuando existe")
    void obtenerPorId_debeRetornarUsuario_cuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));

        UsuarioResponseDTO resultado = securityServiceObtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    private UsuarioResponseDTO securityServiceObtenerPorId(Long id) {
        return usuarioService.obtenerPorId(id);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crearUsuario()
    // ==============================

    @Test
    @DisplayName("crearUsuario - debe crear usuario exitosamente")
    void crearUsuario_debeCrearUsuarioExitosamente() {
        when(usuarioRepository.existsByUsername("ash.ketchum")).thenReturn(false);
        when(usuarioRepository.existsByEmail("ash@kanto.com")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActivo);

        UsuarioResponseDTO resultado = usuarioService.crearUsuario(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("ash.ketchum");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crearUsuario - debe lanzar IllegalStateException cuando el username ya existe")
    void crearUsuario_debeLanzarIllegalStateException_cuandoUsernameDuplicado() {
        when(usuarioRepository.existsByUsername("ash.ketchum")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nombre de usuario ya está registrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearUsuario - debe lanzar IllegalStateException cuando el email ya existe")
    void crearUsuario_debeLanzarIllegalStateException_cuandoEmailDuplicado() {
        when(usuarioRepository.existsByUsername("ash.ketchum")).thenReturn(false);
        when(usuarioRepository.existsByEmail("ash@kanto.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("correo electrónico ya está registrado");

        verify(usuarioRepository, never()).save(any());
    }

    // ==============================
    // actualizarUsuario()
    // ==============================

    @Test
    @DisplayName("actualizarUsuario - debe actualizar usuario exitosamente sin cambiar email ni username")
    void actualizarUsuario_debeActualizarUsuarioExitosamente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActivo);

        UsuarioResponseDTO resultado = usuarioService.actualizarUsuario(1L, requestDTO);

        assertThat(resultado).isNotNull();
        verify(usuarioRepository, never()).existsByUsername(anyString());
        verify(usuarioRepository, never()).existsByEmail(anyString());
    }

    // ==============================
    // desactivarUsuario()
    // ==============================

    @Test
    @DisplayName("desactivarUsuario - debe realizar soft delete desactivando el usuario")
    void desactivarUsuario_debeDesactivarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));

        usuarioService.desactivarUsuario(1L);

        verify(usuarioRepository).save(argThat(u -> !u.getActivo()));
    }

    // ==============================
    // asignarRoles()
    // ==============================

    @Test
    @DisplayName("asignarRoles - debe asignar nuevos roles al usuario")
    void asignarRoles_debeAsignarRoles() {
        Rol rolAdmin = Rol.builder().id(2L).nombre("ROLE_ADMIN").build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActivo);

        UsuarioResponseDTO resultado = usuarioService.asignarRoles(1L, Set.of(2L));

        assertThat(resultado).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
