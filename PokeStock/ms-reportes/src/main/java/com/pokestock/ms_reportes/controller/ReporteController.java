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

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listarReportes() {
        return ResponseEntity.ok(reporteService.listarReportes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    @GetMapping("/inventario")
    public ResponseEntity<ReporteInventarioDTO> reporteInventario() {
        return ResponseEntity.ok(reporteService.generarReporteInventario());
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<ReporteResponseDTO>> reporteMovimientos() {
        return ResponseEntity.ok(reporteService.obtenerMovimientos());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ReporteResponseDTO> reportePorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(reporteService.generarReportePorProducto(productoId));
    }
}