package com.pokestock.ms_reportes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "DTO que representa el reporte resumido del inventario")
public class ReporteInventarioDTO {
    @Schema(description = "Total de productos distintos registrados en el inventario", example = "45")
    private Integer totalProductos;

    @Schema(description = "Suma de todas las unidades físicas de productos en stock", example = "1250")
    private Integer totalUnidades;

    @Schema(description = "Lista detallada de los ítems de inventario")
    private List<ItemInventarioDTO> items;
}