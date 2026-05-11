package com.pokestock.ms_reportes.service;

import com.pokestock.ms_reportes.dto.response.ReporteInventarioDTO;
import com.pokestock.ms_reportes.dto.response.ReporteResponseDTO;

import java.util.List;

public interface ReporteService {

    ReporteInventarioDTO generarReporteInventario();

    List<ReporteResponseDTO> listarReportes();

    ReporteResponseDTO obtenerPorId(Long id);

    List<ReporteResponseDTO> obtenerMovimientos();

    ReporteResponseDTO generarReportePorProducto(Long productoId);
}