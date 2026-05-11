package com.pokestock.ms_productos.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private String edicion;
    private String idioma;
    private Integer anioLanzamiento;
    private Boolean activo;
}