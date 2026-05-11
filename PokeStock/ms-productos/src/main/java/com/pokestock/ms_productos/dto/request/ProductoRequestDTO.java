package com.pokestock.ms_productos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "La edición es obligatoria")
    private String edicion;

    @NotBlank(message = "El idioma es obligatorio")
    private String idioma;

    @NotNull(message = "El año de lanzamiento es obligatorio")
    private Integer anioLanzamiento;
}