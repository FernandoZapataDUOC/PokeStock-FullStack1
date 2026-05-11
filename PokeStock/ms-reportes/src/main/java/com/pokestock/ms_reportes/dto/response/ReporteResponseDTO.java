package com.pokestock.ms_reportes.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReporteResponseDTO {
    private Long id;
    private String tipo;
    private LocalDateTime fechaReporte;
    private String descripcion;
    private String resultadoJson;
}