package com.pokestock.ms_proveedores.service.impl;

import com.pokestock.ms_proveedores.dto.request.ProveedorRequestDTO;
import com.pokestock.ms_proveedores.dto.response.ProveedorResponseDTO;
import com.pokestock.ms_proveedores.model.Proveedor;
import com.pokestock.ms_proveedores.repository.ProveedorRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ProveedorServiceImpl")
class ProveedorServiceImplTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorServiceImpl proveedorService;

    private Proveedor proveedorActivo;
    private Proveedor proveedorInactivo;
    private ProveedorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        proveedorActivo = Proveedor.builder()
                .id(1L)
                .nombre("Distribuidor Pokémon Chile")
                .contacto("Juan Pérez")
                .pais("Chile")
                .email("distribuidor@pokemon.cl")
                .activo(true)
                .build();

        proveedorInactivo = Proveedor.builder()
                .id(2L)
                .nombre("Distribuidor Inactivo")
                .contacto("Ana González")
                .pais("Argentina")
                .email("inactivo@pokemon.ar")
                .activo(false)
                .build();

        requestDTO = new ProveedorRequestDTO();
        requestDTO.setNombre("Distribuidor Pokémon Chile");
        requestDTO.setContacto("Juan Pérez");
        requestDTO.setPais("Chile");
        requestDTO.setEmail("distribuidor@pokemon.cl");
    }

    // ==============================
    // listarTodos()
    // ==============================

    @Test
    @DisplayName("listarTodos - debe retornar todos los proveedores")
    void listarTodos_debeRetornarTodosLosProveedores() {
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedorActivo, proveedorInactivo));

        List<ProveedorResponseDTO> resultado = proveedorService.listarTodos();

        assertThat(resultado).hasSize(2);
        verify(proveedorRepository).findAll();
    }

    // ==============================
    // listarActivos()
    // ==============================

    @Test
    @DisplayName("listarActivos - debe retornar solo proveedores activos")
    void listarActivos_debeRetornarSoloProveedoresActivos() {
        when(proveedorRepository.findByActivoTrue()).thenReturn(List.of(proveedorActivo));

        List<ProveedorResponseDTO> resultado = proveedorService.listarActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar proveedor cuando existe")
    void obtenerPorId_debeRetornarProveedor_cuandoExiste() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));

        ProveedorResponseDTO resultado = proveedorService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Distribuidor Pokémon Chile");
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proveedorService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crear()
    // ==============================

    @Test
    @DisplayName("crear - debe crear proveedor exitosamente cuando el email es único")
    void crear_debeCrearProveedorExitosamente() {
        when(proveedorRepository.findByEmail("distribuidor@pokemon.cl")).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorActivo);

        ProveedorResponseDTO resultado = proveedorService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("distribuidor@pokemon.cl");
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("crear - debe lanzar IllegalStateException cuando el email ya está registrado")
    void crear_debeLanzarIllegalStateException_cuandoEmailDuplicado() {
        when(proveedorRepository.findByEmail("distribuidor@pokemon.cl"))
                .thenReturn(Optional.of(proveedorActivo));

        assertThatThrownBy(() -> proveedorService.crear(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("distribuidor@pokemon.cl");

        verify(proveedorRepository, never()).save(any());
    }

    // ==============================
    // actualizar()
    // ==============================

    @Test
    @DisplayName("actualizar - debe actualizar proveedor exitosamente cuando el email no cambia")
    void actualizar_debeActualizarProveedorExitosamente_cuandoEmailNocambia() {
        ProveedorRequestDTO updateDTO = new ProveedorRequestDTO();
        updateDTO.setNombre("Distribuidor Actualizado");
        updateDTO.setContacto("Pedro Silva");
        updateDTO.setPais("Chile");
        updateDTO.setEmail("distribuidor@pokemon.cl"); // mismo email

        Proveedor actualizado = Proveedor.builder()
                .id(1L).nombre("Distribuidor Actualizado").contacto("Pedro Silva")
                .pais("Chile").email("distribuidor@pokemon.cl").activo(true).build();

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(actualizado);

        ProveedorResponseDTO resultado = proveedorService.actualizar(1L, updateDTO);

        assertThat(resultado.getNombre()).isEqualTo("Distribuidor Actualizado");
        verify(proveedorRepository, never()).findByEmail(anyString()); // No debe validar email si no cambia
    }

    @Test
    @DisplayName("actualizar - debe lanzar IllegalStateException cuando el nuevo email ya existe")
    void actualizar_debeLanzarIllegalStateException_cuandoNuevoEmailDuplicado() {
        ProveedorRequestDTO updateDTO = new ProveedorRequestDTO();
        updateDTO.setNombre("Otro Nombre");
        updateDTO.setContacto("Otro Contacto");
        updateDTO.setPais("Perú");
        updateDTO.setEmail("emailexistente@pokemon.pe"); // email diferente que ya existe

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));
        when(proveedorRepository.findByEmail("emailexistente@pokemon.pe"))
                .thenReturn(Optional.of(proveedorInactivo)); // email ya existe

        assertThatThrownBy(() -> proveedorService.actualizar(1L, updateDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("emailexistente@pokemon.pe");
    }

    @Test
    @DisplayName("actualizar - debe lanzar EntityNotFoundException cuando el proveedor no existe")
    void actualizar_debeLanzarEntityNotFoundException_cuandoProveedorNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proveedorService.actualizar(99L, requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // desactivar()
    // ==============================

    @Test
    @DisplayName("desactivar - debe realizar soft delete correctamente")
    void desactivar_debeRealizarSoftDeleteCorrectamente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorActivo);

        proveedorService.desactivar(1L);

        verify(proveedorRepository).save(argThat(p -> !p.getActivo()));
    }

    @Test
    @DisplayName("desactivar - debe lanzar EntityNotFoundException cuando el proveedor no existe")
    void desactivar_debeLanzarEntityNotFoundException_cuandoProveedorNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proveedorService.desactivar(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
