package com.pokestock.ms_stock.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDTO {

    @Schema(description = "ID único del registro de stock", example = "1")
    private Long id;

    @Schema(description = "ID del producto", example = "1")
    private Long productoId;

    @Schema(description = "Código identificador del lote", example = "LOTE-2026-A")
    private String lote;

    @Schema(description = "Cantidad total de inventario disponible", example = "50")
    private Integer cantidad;

    @Schema(description = "Ubicación en almacén", example = "Pasillo A - Estante 3")
    private String ubicacion;

    @Schema(description = "Fecha de la última actualización física de stock", example = "2026-06-21T15:00:00")
    private LocalDateTime fechaActualizacion;
}