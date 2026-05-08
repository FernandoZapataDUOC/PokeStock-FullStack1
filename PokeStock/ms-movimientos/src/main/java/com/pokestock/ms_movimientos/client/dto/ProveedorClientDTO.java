package com.pokestock.ms_movimientos.client.dto;

import lombok.Data;

@Data
public class ProveedorClientDTO {
    private Long id;
    private String nombre;
    private Boolean activo;
}