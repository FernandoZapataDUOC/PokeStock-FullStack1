package com.pokestock.ms_reportes.controller;

import com.pokestock.ms_reportes.dto.response.ReporteInventarioDTO;
import com.pokestock.ms_reportes.dto.response.ReporteResponseDTO;
import com.pokestock.ms_reportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para la consulta y generación de reportes consolidados de inventario y transacciones")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    @Operation(summary = "Listar reportes generados", description = "Retorna una lista histórica de todos los reportes de auditoría generados en formato JSON.")
    @ApiResponse(responseCode = "200", description = "Historial de reportes obtenido")
    public ResponseEntity<List<ReporteResponseDTO>> listarReportes() {
        return ResponseEntity.ok(reporteService.listarReportes());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener reporte por ID", description = "Obtiene los detalles y el resultado JSON de un reporte específico.")
    @ApiResponse(responseCode = "200", description = "Reporte encontrado")
    @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    public ResponseEntity<ReporteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    @GetMapping("/inventario")
    @Operation(summary = "Generar reporte de inventario actual", description = "Cruza datos en tiempo real de ms-stock y ms-productos para generar la consolidación física del stock.")
    @ApiResponse(responseCode = "200", description = "Reporte de inventario generado")
    public ResponseEntity<ReporteInventarioDTO> reporteInventario() {
        return ResponseEntity.ok(reporteService.generarReporteInventario());
    }

    @GetMapping("/movimientos")
    @Operation(summary = "Obtener reporte de movimientos", description = "Genera e indexa un reporte conteniendo el historial cruzado de movimientos.")
    @ApiResponse(responseCode = "200", description = "Reporte de movimientos generado")
    public ResponseEntity<List<ReporteResponseDTO>> reporteMovimientos() {
        return ResponseEntity.ok(reporteService.obtenerMovimientos());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Generar reporte por producto", description = "Genera un informe detallado con stock, ubicación y catálogo de un producto específico.")
    @ApiResponse(responseCode = "200", description = "Reporte del producto generado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ReporteResponseDTO> reportePorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(reporteService.generarReportePorProducto(productoId));
    }
}