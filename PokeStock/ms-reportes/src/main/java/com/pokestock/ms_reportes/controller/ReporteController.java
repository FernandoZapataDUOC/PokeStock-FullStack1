package com.pokestock.ms_reportes.controller;

import com.pokestock.ms_reportes.dto.response.ReporteInventarioDTO;
import com.pokestock.ms_reportes.dto.response.ReporteResponseDTO;
import com.pokestock.ms_reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    // GET /api/reportes — lista todos los reportes generados
    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listarReportes() {
        return ResponseEntity.ok(reporteService.listarReportes());
    }
    
    // GET /api/reportes/{id} — obtiene un reporte por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    // GET /api/reportes/inventario — cruza ms-stock y ms-productos para stock actual
    @GetMapping("/inventario")
    public ResponseEntity<ReporteInventarioDTO> reporteInventario() {
        return ResponseEntity.ok(reporteService.generarReporteInventario());
    }

    // GET /api/reportes/movimientos — lista todos los reportes de movimientos
    @GetMapping("/movimientos")
    public ResponseEntity<List<ReporteResponseDTO>> reporteMovimientos() {
        return ResponseEntity.ok(reporteService.obtenerMovimientos());
    }

    // GET /api/reportes/producto/{productoId} — detalle de stock por producto específico
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ReporteResponseDTO> reportePorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(reporteService.generarReportePorProducto(productoId));
    }
}