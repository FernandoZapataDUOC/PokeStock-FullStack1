package com.pokestock.ms_reportes.client.dto;

import lombok.Data;

@Data
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String edicion;
    private Boolean activo;
}