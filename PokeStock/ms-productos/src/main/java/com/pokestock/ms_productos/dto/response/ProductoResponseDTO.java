package com.pokestock.ms_productos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    @Schema(description = "ID auto-incremental generado por la base de datos", example = "1")
    private Long id;

    @Schema(description = "Nombre único del producto", example = "Elite Trainer Box Pokémon 151")
    private String nombre;

    @Schema(description = "Tipo de producto de cartas", example = "Elite Trainer Box")
    private String tipo;

    @Schema(description = "Edición o expansión del set de cartas", example = "Scarlet & Violet 151")
    private String edicion;

    @Schema(description = "Idioma de las cartas", example = "Español")
    private String idioma;

    @Schema(description = "Año de publicación oficial", example = "2023")
    private Integer anioLanzamiento;

    @Schema(description = "Indica si el producto está disponible", example = "true")
    private Boolean activo;

    @Schema(description = "Detalles de la categoría del producto")
    private CategoriaResponseDTO categoria;
}