package com.pokestock.ms_validaciones.dto;

import com.pokestock.ms_validaciones.model.Validacion.EstadoValidacion;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionResponseDTO {

    private Long id;
    private Long movimientoId;
    private EstadoValidacion estado;
    private String observacion;
    private LocalDateTime fecha;
}