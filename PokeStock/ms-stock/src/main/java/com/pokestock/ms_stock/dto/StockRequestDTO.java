package com.pokestock.ms_stock.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotBlank(message = "El lote es obligatorio")
    private String lote;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;
}