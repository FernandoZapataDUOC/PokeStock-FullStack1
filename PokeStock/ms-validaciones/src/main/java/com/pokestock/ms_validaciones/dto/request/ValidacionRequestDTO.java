package com.pokestock.ms_validaciones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionRequestDTO {

    @NotNull(message = "El movimientoId es obligatorio")
    @Schema(description = "ID del movimiento a validar", example = "1")
    private Long movimientoId;

    @NotBlank(message = "La observación es obligatoria")
    @Schema(description = "Detalle u observaciones del proceso de validación", example = "Validación cruzada de cantidad y respaldo documental correcta")
    private String observacion;
}