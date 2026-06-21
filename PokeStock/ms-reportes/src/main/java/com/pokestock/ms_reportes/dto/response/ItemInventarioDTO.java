package com.pokestock.ms_reportes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa un ítem detallado en el inventario")
public class ItemInventarioDTO {
    @Schema(description = "ID único del producto", example = "101")
    private Long productoId;

    @Schema(description = "Nombre descriptivo del producto", example = "Pikachu Felpa 20cm")
    private String nombreProducto;

    @Schema(description = "Categoría o tipo de producto", example = "Juguetes")
    private String tipo;

    @Schema(description = "Cantidad total disponible en stock", example = "150")
    private Integer cantidadEnStock;

    @Schema(description = "Ubicación física en el almacén", example = "Estantería A-4")
    private String ubicacion;
}