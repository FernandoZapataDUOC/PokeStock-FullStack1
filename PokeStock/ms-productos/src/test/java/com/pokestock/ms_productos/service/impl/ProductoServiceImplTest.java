package com.pokestock.ms_productos.service.impl;

import com.pokestock.ms_productos.dto.request.ProductoRequestDTO;
import com.pokestock.ms_productos.dto.response.ProductoResponseDTO;
import com.pokestock.ms_productos.model.Categoria;
import com.pokestock.ms_productos.model.Producto;
import com.pokestock.ms_productos.repository.CategoriaRepository;
import com.pokestock.ms_productos.repository.ProductoRepository;
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
@DisplayName("Pruebas unitarias - ProductoServiceImpl")
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoActivo;
    private Categoria categoria;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nombre("Booster Box")
                .descripcion("Cajas de sobres")
                .build();

        productoActivo = Producto.builder()
                .id(1L)
                .nombre("Elite Trainer Box Pokémon 151")
                .tipo("Elite Trainer Box")
                .edicion("Scarlet & Violet 151")
                .idioma("Español")
                .anioLanzamiento(2023)
                .activo(true)
                .categoria(categoria)
                .build();

        requestDTO = ProductoRequestDTO.builder()
                .nombre("Elite Trainer Box Pokémon 151")
                .tipo("Elite Trainer Box")
                .edicion("Scarlet & Violet 151")
                .idioma("Español")
                .anioLanzamiento(2023)
                .categoriaId(1L)
                .build();
    }

    // ==============================
    // listarActivos()
    // ==============================

    @Test
    @DisplayName("listarActivos - debe retornar lista de productos activos")
    void listarActivos_debeRetornarProductosActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(productoActivo));

        List<ProductoResponseDTO> resultado = productoService.listarActivos();

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Elite Trainer Box Pokémon 151");
        verify(productoRepository).findByActivoTrue();
    }

    @Test
    @DisplayName("listarActivos - debe retornar lista vacía cuando no hay productos activos")
    void listarActivos_debeRetornarListaVacia_cuandoNoHayProductosActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of());

        List<ProductoResponseDTO> resultado = productoService.listarActivos();

        assertThat(resultado).isEmpty();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar producto cuando existe")
    void obtenerPorId_debeRetornarProducto_cuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo));

        ProductoResponseDTO resultado = productoService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Elite Trainer Box Pokémon 151");
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crearProducto()
    // ==============================

    @Test
    @DisplayName("crearProducto - debe crear producto exitosamente")
    void crearProducto_debeCrearProductoExitosamente() {
        when(productoRepository.existsByNombreAndEdicion(anyString(), anyString())).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Elite Trainer Box Pokémon 151");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("crearProducto - debe lanzar IllegalStateException cuando el producto ya existe")
    void crearProducto_debeLanzarIllegalStateException_cuandoProductoDuplicado() {
        when(productoRepository.existsByNombreAndEdicion(
                requestDTO.getNombre(), requestDTO.getEdicion())).thenReturn(true);

        assertThatThrownBy(() -> productoService.crearProducto(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Elite Trainer Box Pokémon 151")
                .hasMessageContaining("Scarlet & Violet 151");

        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearProducto - debe lanzar EntityNotFoundException cuando la categoría no existe")
    void crearProducto_debeLanzarEntityNotFoundException_cuandoCategoriaNoExiste() {
        when(productoRepository.existsByNombreAndEdicion(anyString(), anyString())).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crearProducto(requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Categoria no encontrada");
    }

    @Test
    @DisplayName("crearProducto - debe crear producto sin categoría cuando categoriaId es null")
    void crearProducto_debeCrearProducto_sinCategoria() {
        requestDTO.setCategoriaId(null);
        Producto sinCategoria = Producto.builder()
                .id(2L).nombre("Booster Pack").tipo("Booster Pack")
                .edicion("SV Base Set").idioma("Inglés")
                .anioLanzamiento(2023).activo(true).build();

        when(productoRepository.existsByNombreAndEdicion(anyString(), anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(sinCategoria);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertThat(resultado).isNotNull();
        verify(categoriaRepository, never()).findById(any());
    }

    // ==============================
    // actualizarProducto()
    // ==============================

    @Test
    @DisplayName("actualizarProducto - debe actualizar producto exitosamente")
    void actualizarProducto_debeActualizarProductoExitosamente() {
        ProductoRequestDTO updateDTO = ProductoRequestDTO.builder()
                .nombre("ETB Actualizado")
                .tipo("Elite Trainer Box")
                .edicion("Scarlet & Violet 151")
                .idioma("Inglés")
                .anioLanzamiento(2024)
                .categoriaId(null)
                .build();

        Producto actualizado = Producto.builder()
                .id(1L).nombre("ETB Actualizado").tipo("Elite Trainer Box")
                .edicion("Scarlet & Violet 151").idioma("Inglés")
                .anioLanzamiento(2024).activo(true).build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        ProductoResponseDTO resultado = productoService.actualizarProducto(1L, updateDTO);

        assertThat(resultado.getNombre()).isEqualTo("ETB Actualizado");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("actualizarProducto - debe lanzar EntityNotFoundException cuando el producto no existe")
    void actualizarProducto_debeLanzarEntityNotFoundException_cuandoProductoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizarProducto(99L, requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
