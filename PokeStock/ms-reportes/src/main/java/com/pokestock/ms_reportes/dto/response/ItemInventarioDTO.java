package com.pokestock.ms_reportes.dto.response;

import lombok.Data;

@Data
public class ItemInventarioDTO {
    private Long productoId;
    private String nombreProducto;
    private String tipo;
    private Integer cantidadEnStock;
    private String ubicacion;
}