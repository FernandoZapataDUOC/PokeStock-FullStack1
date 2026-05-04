package com.pokestock.ms_proveedores.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorResponseDTO {

    private Long id;
    private String nombre;
    private String contacto;
    private String pais;
    private String email;
    private Boolean activo;
}