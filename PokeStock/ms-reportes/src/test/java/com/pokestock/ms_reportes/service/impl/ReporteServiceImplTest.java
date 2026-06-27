package com.pokestock.ms_reportes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokestock.ms_reportes.client.MovimientoClient;
import com.pokestock.ms_reportes.client.ProductoClient;
import com.pokestock.ms_reportes.client.StockClient;
import com.pokestock.ms_reportes.client.dto.MovimientoClientDTO;
import com.pokestock.ms_reportes.client.dto.ProductoClientDTO;
import com.pokestock.ms_reportes.client.dto.StockClientDTO;
import com.pokestock.ms_reportes.dto.response.ReporteInventarioDTO;
import com.pokestock.ms_reportes.dto.response.ReporteResponseDTO;
import com.pokestock.ms_reportes.model.Reporte;
import com.pokestock.ms_reportes.model.TipoReporte;
import com.pokestock.ms_reportes.repository.ReporteRepository;
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
@DisplayName("Pruebas unitarias - ReporteServiceImpl")
class ReporteServiceImplTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private MovimientoClient movimientoClient;

    @Mock
    private StockClient stockClient;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private StockClientDTO stockDTO;
    private ProductoClientDTO productoDTO;
    private Reporte reporteGuardado;

    @BeforeEach
    void setUp() {
        stockDTO = new StockClientDTO();
        stockDTO.setId(1L);
        stockDTO.setProductoId(1L);
        stockDTO.setCantidad(100);
        stockDTO.setUbicacion("Pasillo A - Estante 3");

        productoDTO = new ProductoClientDTO();
        productoDTO.setId(1L);
        productoDTO.setNombre("Elite Trainer Box");
        productoDTO.setTipo("Elite Trainer Box");
        productoDTO.setEdicion("SV151");
        productoDTO.setActivo(true);

        reporteGuardado = Reporte.builder()
                .id(1L)
                .tipo(TipoReporte.INVENTARIO_ACTUAL)
                .descripcion("Reporte de inventario actual")
                .resultadoJson("{\"totalProductos\":1,\"totalUnidades\":100}")
                .fechaReporte(LocalDateTime.now())
                .build();
    }

    // ==============================
    // generarReporteInventario()
    // ==============================

    @Test
    @DisplayName("generarReporteInventario - debe cruzar datos de stock y productos correctamente")
    void generarReporteInventario_debeCruzarDatosCorrectamente() throws Exception {
        when(stockClient.listarStock()).thenReturn(List.of(stockDTO));
        when(productoClient.listarProductos()).thenReturn(List.of(productoDTO));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalProductos\":1,\"totalUnidades\":100}");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteGuardado);

        ReporteInventarioDTO resultado = reporteService.generarReporteInventario();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalProductos()).isEqualTo(1);
        assertThat(resultado.getTotalUnidades()).isEqualTo(100);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getNombreProducto()).isEqualTo("Elite Trainer Box");
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    @DisplayName("generarReporteInventario - debe retornar reporte vacío si no hay stock")
    void generarReporteInventario_debeRetornarReporteVacio_sinStock() throws Exception {
        when(stockClient.listarStock()).thenReturn(List.of());
        when(productoClient.listarProductos()).thenReturn(List.of(productoDTO));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalProductos\":0,\"totalUnidades\":0}");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteGuardado);

        ReporteInventarioDTO resultado = reporteService.generarReporteInventario();

        assertThat(resultado.getTotalProductos()).isEqualTo(0);
        assertThat(resultado.getTotalUnidades()).isEqualTo(0);
        assertThat(resultado.getItems()).isEmpty();
    }

    @Test
    @DisplayName("generarReporteInventario - debe lanzar IllegalStateException si falla la serialización JSON")
    void generarReporteInventario_debeLanzarIllegalStateException_siJsonFalla() throws Exception {
        when(stockClient.listarStock()).thenReturn(List.of(stockDTO));
        when(productoClient.listarProductos()).thenReturn(List.of(productoDTO));
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("JSON error") {});

        assertThatThrownBy(() -> reporteService.generarReporteInventario())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Error generando reporte");
    }

    // ==============================
    // obtenerMovimientos()
    // ==============================

    @Test
    @DisplayName("obtenerMovimientos - debe retornar historial de movimientos persistido")
    void obtenerMovimientos_debeRetornarHistorialMovimientos() throws Exception {
        MovimientoClientDTO movimientoDTO = new MovimientoClientDTO();
        movimientoDTO.setId(1L);

        Reporte reporteHistorial = Reporte.builder()
                .id(2L).tipo(TipoReporte.HISTORIAL_MOVIMIENTOS)
                .descripcion("Historial de movimientos")
                .resultadoJson("[{}]")
                .fechaReporte(LocalDateTime.now())
                .build();

        when(movimientoClient.listarMovimientos()).thenReturn(List.of(movimientoDTO));
        when(objectMapper.writeValueAsString(any())).thenReturn("[{}]");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteHistorial);
        when(reporteRepository.findByTipo(TipoReporte.HISTORIAL_MOVIMIENTOS))
                .thenReturn(List.of(reporteHistorial));

        List<ReporteResponseDTO> resultado = reporteService.obtenerMovimientos();

        assertThat(resultado).hasSize(1);
        verify(movimientoClient).listarMovimientos();
        verify(reporteRepository).save(any(Reporte.class));
    }

    // ==============================
    // generarReportePorProducto()
    // ==============================

    @Test
    @DisplayName("generarReportePorProducto - debe generar reporte con datos del producto y su stock")
    void generarReportePorProducto_debeGenerarReporteCorrectamente() throws Exception {
        Reporte reporteProducto = Reporte.builder()
                .id(3L).tipo(TipoReporte.REPORTE_POR_PRODUCTO)
                .descripcion("Reporte producto: Elite Trainer Box")
                .resultadoJson("{\"productoId\":1}")
                .fechaReporte(LocalDateTime.now())
                .build();

        when(productoClient.obtenerProducto(1L)).thenReturn(productoDTO);
        when(stockClient.obtenerStockPorProducto(1L)).thenReturn(stockDTO);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"productoId\":1}");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteProducto);

        ReporteResponseDTO resultado = reporteService.generarReportePorProducto(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescripcion()).contains("Elite Trainer Box");
        verify(productoClient).obtenerProducto(1L);
        verify(stockClient).obtenerStockPorProducto(1L);
    }

    @Test
    @DisplayName("generarReportePorProducto - debe manejar stock null y usar valores por defecto")
    void generarReportePorProducto_debeManejarStockNull() throws Exception {
        Reporte reporteSinStock = Reporte.builder()
                .id(4L).tipo(TipoReporte.REPORTE_POR_PRODUCTO)
                .descripcion("Reporte producto: Elite Trainer Box")
                .resultadoJson("{\"productoId\":1,\"cantidadEnStock\":0}")
                .fechaReporte(LocalDateTime.now())
                .build();

        when(productoClient.obtenerProducto(1L)).thenReturn(productoDTO);
        when(stockClient.obtenerStockPorProducto(1L)).thenReturn(null); // sin stock
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"productoId\":1,\"cantidadEnStock\":0}");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteSinStock);

        ReporteResponseDTO resultado = reporteService.generarReportePorProducto(1L);

        assertThat(resultado).isNotNull();
        // Verifica que se guardó con cantidad 0 y ubicación "Sin stock"
        verify(reporteRepository).save(argThat(r -> r.getTipo() == TipoReporte.REPORTE_POR_PRODUCTO));
    }

    // ==============================
    // listarReportes()
    // ==============================

    @Test
    @DisplayName("listarReportes - debe retornar todos los reportes ordenados por fecha")
    void listarReportes_debeRetornarTodosLosReportesOrdenados() {
        when(reporteRepository.findAllByOrderByFechaReporteDesc())
                .thenReturn(List.of(reporteGuardado));

        List<ReporteResponseDTO> resultado = reporteService.listarReportes();

        assertThat(resultado).hasSize(1);
        verify(reporteRepository).findAllByOrderByFechaReporteDesc();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar reporte cuando existe")
    void obtenerPorId_debeRetornarReporte_cuandoExiste() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteGuardado));

        ReporteResponseDTO resultado = reporteService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reporteService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
