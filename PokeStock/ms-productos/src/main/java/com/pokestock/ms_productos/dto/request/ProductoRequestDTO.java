package com.pokestock.ms_productos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre único del producto", example = "Elite Trainer Box Pokémon 151")
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio")
    @Schema(description = "Tipo de producto de cartas (ej. ETB, Booster Pack, Single Card)", example = "Elite Trainer Box")
    private String tipo;

    @NotBlank(message = "La edición es obligatoria")
    @Schema(description = "Edición o expansión del set de cartas", example = "Scarlet & Violet 151")
    private String edicion;

    @NotBlank(message = "El idioma es obligatorio")
    @Schema(description = "Idioma de las cartas del producto", example = "Español")
    private String idioma;

    @NotNull(message = "El año de lanzamiento es obligatorio")
    @Schema(description = "Año de publicación oficial del producto", example = "2023")
    private Integer anioLanzamiento;

    @Schema(description = "ID de la categoría a la cual pertenece", example = "1")
    private Long categoriaId;
}