package com.pokestock.ms_stock.service.Impl;

import com.pokestock.ms_stock.dto.request.StockRequestDTO;
import com.pokestock.ms_stock.dto.response.StockResponseDTO;
import com.pokestock.ms_stock.model.Stock;
import com.pokestock.ms_stock.repository.StockRepository;
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
@DisplayName("Pruebas unitarias - StockServiceImpl")
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        stock = Stock.builder()
                .id(1L)
                .productoId(1L)
                .lote("LOTE-2026-A")
                .cantidad(100)
                .ubicacion("Pasillo A - Estante 3")
                .build();

        requestDTO = StockRequestDTO.builder()
                .productoId(1L)
                .lote("LOTE-2026-A")
                .cantidad(100)
                .ubicacion("Pasillo A - Estante 3")
                .build();
    }

    // ==============================
    // listarTodo()
    // ==============================

    @Test
    @DisplayName("listarTodo - debe retornar todo el stock")
    void listarTodo_debeRetornarTodoElStock() {
        when(stockRepository.findAll()).thenReturn(List.of(stock));

        List<StockResponseDTO> resultado = stockService.listarTodo();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(1L);
        verify(stockRepository).findAll();
    }

    // ==============================
    // buscarPorProducto()
    // ==============================

    @Test
    @DisplayName("buscarPorProducto - debe retornar stock del producto indicado")
    void buscarPorProducto_debeRetornarStock_delProductoIndicado() {
        when(stockRepository.findByProductoId(1L)).thenReturn(List.of(stock));

        List<StockResponseDTO> resultado = stockService.buscarPorProducto(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLote()).isEqualTo("LOTE-2026-A");
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar stock cuando existe")
    void obtenerPorId_debeRetornarStock_cuandoExiste() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        StockResponseDTO resultado = stockService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCantidad()).isEqualTo(100);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crearStock()
    // ==============================

    @Test
    @DisplayName("crearStock - debe crear registro de stock exitosamente")
    void crearStock_debeCrearStockExitosamente() {
        when(stockRepository.findByProductoIdAndLote(1L, "LOTE-2026-A")).thenReturn(Optional.empty());
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        StockResponseDTO resultado = stockService.crearStock(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getLote()).isEqualTo("LOTE-2026-A");
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("crearStock - debe lanzar IllegalStateException cuando el lote ya existe para ese producto")
    void crearStock_debeLanzarIllegalStateException_cuandoLoteDuplicado() {
        when(stockRepository.findByProductoIdAndLote(1L, "LOTE-2026-A")).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> stockService.crearStock(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("LOTE-2026-A");

        verify(stockRepository, never()).save(any());
    }

    // ==============================
    // aumentarStock()
    // ==============================

    @Test
    @DisplayName("aumentarStock - debe aumentar la cantidad correctamente")
    void aumentarStock_debeAumentarCantidadCorrectamente() {
        Stock stockActualizado = Stock.builder()
                .id(1L).productoId(1L).lote("LOTE-2026-A")
                .cantidad(110).ubicacion("Pasillo A - Estante 3").build();

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stockActualizado);

        StockResponseDTO resultado = stockService.aumentarStock(1L, 10);

        assertThat(resultado.getCantidad()).isEqualTo(110);
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("aumentarStock - debe lanzar IllegalArgumentException cuando la cantidad es cero o negativa")
    void aumentarStock_debeLanzarIllegalArgumentException_cuandoCantidadInvalida() {
        assertThatThrownBy(() -> stockService.aumentarStock(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor a 0");

        assertThatThrownBy(() -> stockService.aumentarStock(1L, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor a 0");

        verify(stockRepository, never()).save(any());
    }

    @Test
    @DisplayName("aumentarStock - debe lanzar EntityNotFoundException cuando el stock no existe")
    void aumentarStock_debeLanzarEntityNotFoundException_cuandoStockNoExiste() {
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.aumentarStock(99L, 10))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // descontarStock()
    // ==============================

    @Test
    @DisplayName("descontarStock - debe descontar la cantidad correctamente")
    void descontarStock_debeDescontarCantidadCorrectamente() {
        Stock stockActualizado = Stock.builder()
                .id(1L).productoId(1L).lote("LOTE-2026-A")
                .cantidad(80).ubicacion("Pasillo A - Estante 3").build();

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stockActualizado);

        StockResponseDTO resultado = stockService.descontarStock(1L, 20);

        assertThat(resultado.getCantidad()).isEqualTo(80);
    }

    @Test
    @DisplayName("descontarStock - debe lanzar IllegalArgumentException cuando la cantidad es cero o negativa")
    void descontarStock_debeLanzarIllegalArgumentException_cuandoCantidadInvalida() {
        assertThatThrownBy(() -> stockService.descontarStock(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor a 0");
    }

    @Test
    @DisplayName("descontarStock - debe lanzar IllegalStateException cuando el stock es insuficiente")
    void descontarStock_debeLanzarIllegalStateException_cuandoStockInsuficiente() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock)); // stock.getCantidad() = 100

        assertThatThrownBy(() -> stockService.descontarStock(1L, 200)) // solicita 200, hay 100
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    @DisplayName("descontarStock - debe lanzar EntityNotFoundException cuando el stock no existe")
    void descontarStock_debeLanzarEntityNotFoundException_cuandoStockNoExiste() {
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.descontarStock(99L, 10))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
