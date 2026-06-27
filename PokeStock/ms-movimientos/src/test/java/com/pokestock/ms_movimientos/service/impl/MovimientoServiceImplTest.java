package com.pokestock.ms_movimientos.service.impl;

import com.pokestock.ms_movimientos.client.*;
import com.pokestock.ms_movimientos.client.dto.*;
import com.pokestock.ms_movimientos.dto.request.MovimientoRequestDTO;
import com.pokestock.ms_movimientos.dto.response.MovimientoResponseDTO;
import com.pokestock.ms_movimientos.model.EstadoMovimiento;
import com.pokestock.ms_movimientos.model.Movimiento;
import com.pokestock.ms_movimientos.model.TipoMovimiento;
import com.pokestock.ms_movimientos.repository.MovimientosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - MovimientoServiceImpl")
class MovimientoServiceImplTest {

    @Mock
    private MovimientosRepository movimientoRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private ProveedorClient proveedorClient;

    @Mock
    private StockClient stockClient;

    @Mock
    private DocumentoClient documentoClient;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private ProductoClientDTO productoActivo;
    private ProveedorClientDTO proveedorActivo;
    private StockClientDTO stockDisponible;
    private Movimiento movimientoPendiente;
    private Movimiento movimientoValidado;

    @BeforeEach
    void setUp() {
        productoActivo = new ProductoClientDTO();
        productoActivo.setId(1L);
        productoActivo.setNombre("Elite Trainer Box");
        productoActivo.setActivo(true);

        proveedorActivo = new ProveedorClientDTO();
        proveedorActivo.setId(1L);
        proveedorActivo.setNombre("Distribuidor Pokémon Chile");
        proveedorActivo.setActivo(true);

        stockDisponible = new StockClientDTO();
        stockDisponible.setId(1L);
        stockDisponible.setProductoId(1L);
        stockDisponible.setCantidad(100);
        stockDisponible.setLote("LOTE-2026-A");

        movimientoPendiente = Movimiento.builder()
                .id(1L)
                .productoId(1L)
                .proveedorId(1L)
                .tipo(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .estado(EstadoMovimiento.PENDIENTE)
                .observacion("Ingreso de stock")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        movimientoValidado = Movimiento.builder()
                .id(2L)
                .productoId(1L)
                .proveedorId(1L)
                .tipo(TipoMovimiento.SALIDA)
                .cantidad(5)
                .estado(EstadoMovimiento.VALIDADO)
                .observacion("Salida de stock")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    // ==============================
    // listarTodos()
    // ==============================

    @Test
    @DisplayName("listarTodos - debe retornar todos los movimientos")
    void listarTodos_debeRetornarTodosLosMovimientos() {
        when(movimientoRepository.findAll()).thenReturn(List.of(movimientoPendiente, movimientoValidado));

        List<MovimientoResponseDTO> resultado = movimientoService.listarTodos();

        assertThat(resultado).hasSize(2);
        verify(movimientoRepository).findAll();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar movimiento cuando existe")
    void obtenerPorId_debeRetornarMovimiento_cuandoExiste() {
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimientoPendiente));

        MovimientoResponseDTO resultado = movimientoService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // crear() - ENTRADA
    // ==============================

    @Test
    @DisplayName("crear ENTRADA - debe crear el movimiento exitosamente validando producto y proveedor")
    void crear_debeCrearMovimientoEntrada_exitosamente() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setProveedorId(1L);
        dto.setTipo(TipoMovimiento.ENTRADA);
        dto.setCantidad(10);
        dto.setObservacion("Ingreso de stock");

        when(productoClient.obtenerProducto(1L)).thenReturn(productoActivo);
        when(proveedorClient.obtenerProveedor(1L)).thenReturn(proveedorActivo);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoPendiente);

        MovimientoResponseDTO resultado = movimientoService.crear(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo(TipoMovimiento.ENTRADA);
        verify(productoClient).obtenerProducto(1L);
        verify(proveedorClient).obtenerProveedor(1L);
        verify(stockClient, never()).obtenerStockPorProducto(any()); // ENTRADA no valida stock
    }

    @Test
    @DisplayName("crear - debe lanzar EntityNotFoundException cuando el producto está inactivo")
    void crear_debeLanzarEntityNotFoundException_cuandoProductoInactivo() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setProveedorId(1L);
        dto.setTipo(TipoMovimiento.ENTRADA);
        dto.setCantidad(10);

        ProductoClientDTO productoInactivo = new ProductoClientDTO();
        productoInactivo.setId(1L);
        productoInactivo.setActivo(false);

        when(productoClient.obtenerProducto(1L)).thenReturn(productoInactivo);

        assertThatThrownBy(() -> movimientoService.crear(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear - debe lanzar IllegalStateException cuando el servicio de producto no está disponible")
    void crear_debeLanzarIllegalStateException_cuandoServicioProductoNoDisponible() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setProveedorId(1L);
        dto.setTipo(TipoMovimiento.ENTRADA);
        dto.setCantidad(10);

        when(productoClient.obtenerProducto(1L)).thenThrow(new RuntimeException("Connection refused"));

        assertThatThrownBy(() -> movimientoService.crear(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no disponible");
    }

    // ==============================
    // crear() - SALIDA
    // ==============================

    @Test
    @DisplayName("crear SALIDA - debe crear el movimiento exitosamente cuando hay stock suficiente")
    void crear_debeCrearMovimientoSalida_cuandoHayStockSuficiente() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setProveedorId(1L);
        dto.setTipo(TipoMovimiento.SALIDA);
        dto.setCantidad(10); // hay 100 disponibles

        Movimiento movimientoSalida = Movimiento.builder()
                .id(3L).productoId(1L).proveedorId(1L)
                .tipo(TipoMovimiento.SALIDA).cantidad(10)
                .estado(EstadoMovimiento.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        when(productoClient.obtenerProducto(1L)).thenReturn(productoActivo);
        when(proveedorClient.obtenerProveedor(1L)).thenReturn(proveedorActivo);
        when(stockClient.obtenerStockPorProducto(1L)).thenReturn(List.of(stockDisponible));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoSalida);

        MovimientoResponseDTO resultado = movimientoService.crear(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo(TipoMovimiento.SALIDA);
        verify(stockClient).obtenerStockPorProducto(1L);
    }

    @Test
    @DisplayName("crear SALIDA - debe lanzar IllegalArgumentException cuando el stock es insuficiente")
    void crear_debeLanzarIllegalArgumentException_cuandoStockInsuficiente() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setProveedorId(1L);
        dto.setTipo(TipoMovimiento.SALIDA);
        dto.setCantidad(200); // solicita 200, hay 100

        when(productoClient.obtenerProducto(1L)).thenReturn(productoActivo);
        when(proveedorClient.obtenerProveedor(1L)).thenReturn(proveedorActivo);
        when(stockClient.obtenerStockPorProducto(1L)).thenReturn(List.of(stockDisponible));

        assertThatThrownBy(() -> movimientoService.crear(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(movimientoRepository, never()).save(any());
    }

    // ==============================
    // validar()
    // ==============================

    @Test
    @DisplayName("validar - debe cambiar estado a VALIDADO cuando tiene documentos asociados")
    void validar_debeCambiarEstadoAValidado_cuandoTieneDocumentos() {
        DocumentoClientDTO documento = new DocumentoClientDTO();
        documento.setId(1L);
        documento.setMovimientoId(1L);

        Movimiento movimientoValidadoResult = Movimiento.builder()
                .id(1L).productoId(1L).proveedorId(1L)
                .tipo(TipoMovimiento.ENTRADA).cantidad(10)
                .estado(EstadoMovimiento.VALIDADO)
                .fechaCreacion(LocalDateTime.now()).fechaActualizacion(LocalDateTime.now())
                .build();

        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimientoPendiente));
        when(documentoClient.obtenerDocumentosPorMovimiento(1L)).thenReturn(List.of(documento));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoValidadoResult);

        MovimientoResponseDTO resultado = movimientoService.validar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoMovimiento.VALIDADO);
    }

    @Test
    @DisplayName("validar - debe lanzar IllegalStateException cuando el movimiento no está en estado PENDIENTE")
    void validar_debeLanzarIllegalStateException_cuandoMovimientoNoEsPendiente() {
        when(movimientoRepository.findById(2L)).thenReturn(Optional.of(movimientoValidado)); // VALIDADO

        assertThatThrownBy(() -> movimientoService.validar(2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDIENTE");
    }

    @Test
    @DisplayName("validar - debe lanzar IllegalArgumentException cuando no hay documentos asociados")
    void validar_debeLanzarIllegalArgumentException_cuandoNoHayDocumentos() {
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimientoPendiente));
        when(documentoClient.obtenerDocumentosPorMovimiento(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> movimientoService.validar(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("documentos");
    }

    // ==============================
    // rechazar()
    // ==============================

    @Test
    @DisplayName("rechazar - debe lanzar IllegalStateException cuando el movimiento ya está completado")
    void rechazar_debeLanzarIllegalStateException_cuandoMovimientoYaCompletado() {
        Movimiento movimientoCompletado = Movimiento.builder()
                .id(5L).productoId(1L).proveedorId(1L)
                .tipo(TipoMovimiento.ENTRADA).cantidad(10)
                .estado(EstadoMovimiento.COMPLETADO)
                .fechaCreacion(LocalDateTime.now()).fechaActualizacion(LocalDateTime.now())
                .build();

        when(movimientoRepository.findById(5L)).thenReturn(Optional.of(movimientoCompletado));

        assertThatThrownBy(() -> movimientoService.rechazar(5L, "Motivo de rechazo"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("completado");
    }
}
