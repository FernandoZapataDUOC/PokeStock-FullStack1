package com.pokestock.ms_validaciones.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionRequestDTO {

    @NotNull(message = "El movimientoId es obligatorio")
    private Long movimientoId;

    @NotBlank(message = "La observación es obligatoria")
    private String observacion;
}