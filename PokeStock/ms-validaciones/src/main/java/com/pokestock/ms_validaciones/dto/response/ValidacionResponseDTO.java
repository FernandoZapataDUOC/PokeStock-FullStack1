package com.pokestock.ms_validaciones.dto.response;

import com.pokestock.ms_validaciones.model.Validacion.EstadoValidacion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionResponseDTO {

    @Schema(description = "ID único de la auditoría de validación", example = "1")
    private Long id;

    @Schema(description = "ID del movimiento auditado", example = "1")
    private Long movimientoId;

    @Schema(description = "Resultado del estado de validación", example = "APROBADO")
    private EstadoValidacion estado;

    @Schema(description = "Anotaciones u observaciones de la validación", example = "Validación cruzada de cantidad y respaldo documental correcta")
    private String observacion;

    @Schema(description = "Fecha y hora del procesamiento de auditoría", example = "2026-06-21T15:10:00")
    private LocalDateTime fecha;
}