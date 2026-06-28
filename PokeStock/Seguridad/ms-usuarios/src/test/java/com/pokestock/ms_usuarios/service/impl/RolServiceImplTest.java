package com.pokestock.ms_usuarios.service.impl;

import com.pokestock.ms_usuarios.dto.request.RolRequestDTO;
import com.pokestock.ms_usuarios.dto.response.RolResponseDTO;
import com.pokestock.ms_usuarios.model.Rol;
import com.pokestock.ms_usuarios.repository.RolRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - RolServiceImpl")
class RolServiceImplTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rolAdmin;
    private RolRequestDTO rolRequest;

    @BeforeEach
    void setUp() {
        rolAdmin = Rol.builder()
                .id(1L)
                .nombre("ROLE_ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        rolRequest = RolRequestDTO.builder()
                .nombre("ROLE_ADMIN")
                .descripcion("Administrador del sistema")
                .build();
    }

    // ==============================
    // listarTodos()
    // ==============================

    @Test
    @DisplayName("listarTodos - debe retornar todos los roles")
    void listarTodos_debeRetornarTodosLosRoles() {
        when(rolRepository.findAll()).thenReturn(List.of(rolAdmin));

        List<RolResponseDTO> resultado = rolService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("ROLE_ADMIN");
        verify(rolRepository).findAll();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar rol cuando existe")
    void obtenerPorId_debeRetornarRol_cuandoExiste() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));

        RolResponseDTO resultado = rolService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crearRol()
    // ==============================

    @Test
    @DisplayName("crearRol - debe crear rol exitosamente")
    void crearRol_debeCrearRolExitosamente() {
        when(rolRepository.existsByNombre("ROLE_ADMIN")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenReturn(rolAdmin);

        RolResponseDTO resultado = rolService.crearRol(rolRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("ROLE_ADMIN");
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    @DisplayName("crearRol - debe lanzar IllegalStateException cuando el nombre del rol ya existe")
    void crearRol_debeLanzarIllegalStateException_cuandoRolDuplicado() {
        when(rolRepository.existsByNombre("ROLE_ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> rolService.crearRol(rolRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ROLE_ADMIN");

        verify(rolRepository, never()).save(any());
    }

    // ==============================
    // eliminarRol()
    // ==============================

    @Test
    @DisplayName("eliminarRol - debe eliminar rol cuando existe")
    void eliminarRol_debeEliminarRol_cuandoExiste() {
        when(rolRepository.existsById(1L)).thenReturn(true);

        rolService.eliminarRol(1L);

        verify(rolRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarRol - debe lanzar EntityNotFoundException cuando el rol no existe")
    void eliminarRol_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(rolRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> rolService.eliminarRol(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(rolRepository, never()).deleteById(anyLong());
    }
}
