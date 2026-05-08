package com.pokestock.ms_movimientos.client.dto;

import lombok.Data;

@Data
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private Boolean activo;
}