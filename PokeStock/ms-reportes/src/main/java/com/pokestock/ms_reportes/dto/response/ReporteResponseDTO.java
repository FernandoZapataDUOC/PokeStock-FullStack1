package com.pokestock.ms_reportes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO que representa la respuesta detallada de la entidad Reporte")
public class ReporteResponseDTO {
    @Schema(description = "ID único del reporte generado", example = "10")
    private Long id;

    @Schema(description = "Tipo de reporte generado", example = "INVENTARIO_GENERAL")
    private String tipo;

    @Schema(description = "Fecha y hora de generación del reporte", example = "2026-06-21T15:30:00")
    private LocalDateTime fechaReporte;

    @Schema(description = "Breve descripción de los parámetros o alcance del reporte", example = "Reporte de stock consolidado para todas las ubicaciones")
    private String descripcion;

    @Schema(description = "Detalle del reporte estructurado en formato JSON", example = "{\"totalProductos\":45,\"totalUnidades\":1250}")
    private String resultadoJson;
}