package com.pokestock.ms_stock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    @Schema(description = "ID del producto al cual asignar stock", example = "1")
    private Long productoId;

    @NotBlank(message = "El lote es obligatorio")
    @Schema(description = "Código de lote de fabricación/ingreso del producto", example = "LOTE-2026-A")
    private String lote;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Schema(description = "Cantidad física disponible de unidades", example = "50")
    private Integer cantidad;

    @NotBlank(message = "La ubicación es obligatoria")
    @Schema(description = "Ubicación o estantería dentro de la bodega principal", example = "Pasillo A - Estante 3")
    private String ubicacion;
}