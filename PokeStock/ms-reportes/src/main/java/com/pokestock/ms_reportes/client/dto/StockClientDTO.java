package com.pokestock.ms_reportes.client.dto;

import lombok.Data;

@Data
public class StockClientDTO {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private String ubicacion;
}