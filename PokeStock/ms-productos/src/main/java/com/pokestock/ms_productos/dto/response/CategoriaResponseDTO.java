package com.pokestock.ms_productos.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
}