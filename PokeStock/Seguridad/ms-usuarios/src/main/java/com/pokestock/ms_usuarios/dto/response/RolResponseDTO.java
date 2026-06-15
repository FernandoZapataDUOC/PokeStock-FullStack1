package com.pokestock.ms_usuarios.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
}
