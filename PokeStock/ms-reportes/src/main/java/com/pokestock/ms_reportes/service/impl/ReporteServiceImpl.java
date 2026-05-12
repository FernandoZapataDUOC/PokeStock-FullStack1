package com.pokestock.ms_reportes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokestock.ms_reportes.client.MovimientoClient;
import com.pokestock.ms_reportes.client.ProductoClient;
import com.pokestock.ms_reportes.client.StockClient;
import com.pokestock.ms_reportes.client.dto.MovimientoClientDTO;
import com.pokestock.ms_reportes.client.dto.ProductoClientDTO;
import com.pokestock.ms_reportes.client.dto.StockClientDTO;
import com.pokestock.ms_reportes.dto.response.ItemInventarioDTO;
import com.pokestock.ms_reportes.dto.response.ReporteInventarioDTO;
import com.pokestock.ms_reportes.dto.response.ReporteResponseDTO;
import com.pokestock.ms_reportes.model.Reporte;
import com.pokestock.ms_reportes.model.TipoReporte;
import com.pokestock.ms_reportes.repository.ReporteRepository;
import com.pokestock.ms_reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final MovimientoClient movimientoClient;
    private final StockClient stockClient;
    private final ProductoClient productoClient;
    private final ObjectMapper objectMapper;

    @Override
    public ReporteInventarioDTO generarReporteInventario() {
        log.info("Generando reporte de inventario actual");

        List<StockClientDTO> stocks = stockClient.listarStock();
        List<ProductoClientDTO> productos = productoClient.listarProductos();

        // Se cruzan datos de ms-stock con ms-productos para obtener nombres legibles.
        // ms-stock solo conoce productoId — ms-productos provee el nombre y tipo.
        List<ItemInventarioDTO> items = stocks.stream().map(stock -> {
            ItemInventarioDTO item = new ItemInventarioDTO();
            item.setProductoId(stock.getProductoId());
            item.setCantidadEnStock(stock.getCantidad());
            item.setUbicacion(stock.getUbicacion());

            // Buscar nombre del producto
            productos.stream()
                    .filter(p -> p.getId().equals(stock.getProductoId()))
                    .findFirst()
                    .ifPresent(p -> {
                        item.setNombreProducto(p.getNombre());
                        item.setTipo(p.getTipo());
                    });

            return item;
        }).collect(Collectors.toList());

        ReporteInventarioDTO reporte = new ReporteInventarioDTO();
        reporte.setTotalProductos(items.size());
        reporte.setTotalUnidades(
            items.stream().mapToInt(ItemInventarioDTO::getCantidadEnStock).sum()
        );
        reporte.setItems(items);

        // Persistir el reporte generado
        guardarReporte(TipoReporte.INVENTARIO_ACTUAL, reporte,
                "Reporte de inventario actual");

        return reporte;
    }

    @Override
    public List<ReporteResponseDTO> obtenerMovimientos() {
        log.info("Obteniendo historial de movimientos");
        List<MovimientoClientDTO> movimientos = movimientoClient.listarMovimientos();
        guardarReporte(TipoReporte.HISTORIAL_MOVIMIENTOS, movimientos,
                "Historial de movimientos");
        return reporteRepository.findByTipo(TipoReporte.HISTORIAL_MOVIMIENTOS)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReporteResponseDTO generarReportePorProducto(Long productoId) {
        log.info("Generando reporte para producto id: {}", productoId);

        ProductoClientDTO producto = productoClient.obtenerProducto(productoId);
        StockClientDTO stock = stockClient.obtenerStockPorProducto(productoId);

        ItemInventarioDTO item = new ItemInventarioDTO();
        item.setProductoId(productoId);
        item.setNombreProducto(producto.getNombre());
        item.setTipo(producto.getTipo());
        item.setCantidadEnStock(stock != null ? stock.getCantidad() : 0);
        item.setUbicacion(stock != null ? stock.getUbicacion() : "Sin stock");

        Reporte reporte = guardarReporte(TipoReporte.REPORTE_POR_PRODUCTO, item,
                "Reporte producto: " + producto.getNombre());

        return toResponse(reporte);
    }

    @Override
    public List<ReporteResponseDTO> listarReportes() {
        return reporteRepository.findAllByOrderByFechaReporteDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReporteResponseDTO obtenerPorId(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        return toResponse(reporte);
    }

    // Los resultados de cada reporte se serializan como JSON y se persisten en la BD.
    // Esto permite auditoría histórica: se puede ver cómo estaba el inventario
    // en una fecha pasada sin necesidad de recalcular.
    private Reporte guardarReporte(TipoReporte tipo, Object data, String descripcion) {
        try {
            String json = objectMapper.writeValueAsString(data);
            Reporte reporte = Reporte.builder()
                    .tipo(tipo)
                    .descripcion(descripcion)
                    .resultadoJson(json)
                    .build();
            return reporteRepository.save(reporte);
        } catch (JsonProcessingException e) {
            log.error("Error serializando reporte: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte");
        }
    }

    private ReporteResponseDTO toResponse(Reporte r) {
        ReporteResponseDTO dto = new ReporteResponseDTO();
        dto.setId(r.getId());
        dto.setTipo(r.getTipo().name());
        dto.setFechaReporte(r.getFechaReporte());
        dto.setDescripcion(r.getDescripcion());
        dto.setResultadoJson(r.getResultadoJson());
        return dto;
    }
}