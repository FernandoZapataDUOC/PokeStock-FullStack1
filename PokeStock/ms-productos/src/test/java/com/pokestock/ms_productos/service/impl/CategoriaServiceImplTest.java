package com.pokestock.ms_productos.service.impl;

import com.pokestock.ms_productos.dto.request.CategoriaRequestDTO;
import com.pokestock.ms_productos.dto.response.CategoriaResponseDTO;
import com.pokestock.ms_productos.model.Categoria;
import com.pokestock.ms_productos.repository.CategoriaRepository;
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
@DisplayName("Pruebas unitarias - CategoriaServiceImpl")
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nombre("Booster Box")
                .descripcion("Cajas de sobres de cartas Pokémon")
                .build();

        requestDTO = CategoriaRequestDTO.builder()
                .nombre("Booster Box")
                .descripcion("Cajas de sobres de cartas Pokémon")
                .build();
    }

    // ==============================
    // listarTodas()
    // ==============================

    @Test
    @DisplayName("listarTodas - debe retornar todas las categorías")
    void listarTodas_debeRetornarTodasLasCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Booster Box");
        verify(categoriaRepository).findAll();
    }

    @Test
    @DisplayName("listarTodas - debe retornar lista vacía cuando no hay categorías")
    void listarTodas_debeRetornarListaVacia_cuandoNoHayCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

        assertThat(resultado).isEmpty();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar categoría cuando existe")
    void obtenerPorId_debeRetornarCategoria_cuandoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO resultado = categoriaService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Booster Box");
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crearCategoria()
    // ==============================

    @Test
    @DisplayName("crearCategoria - debe crear categoría exitosamente")
    void crearCategoria_debeCrearCategoriaExitosamente() {
        when(categoriaRepository.existsByNombre("Booster Box")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDTO resultado = categoriaService.crearCategoria(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Booster Box");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("crearCategoria - debe lanzar IllegalStateException cuando el nombre ya existe")
    void crearCategoria_debeLanzarIllegalStateException_cuandoNombreDuplicado() {
        when(categoriaRepository.existsByNombre("Booster Box")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.crearCategoria(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Booster Box");

        verify(categoriaRepository, never()).save(any());
    }
}
