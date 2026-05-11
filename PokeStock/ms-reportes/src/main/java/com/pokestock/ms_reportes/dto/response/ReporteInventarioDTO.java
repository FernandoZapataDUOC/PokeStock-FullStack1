package com.pokestock.ms_reportes.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ReporteInventarioDTO {
    private Integer totalProductos;
    private Integer totalUnidades;
    private List<ItemInventarioDTO> items;
}