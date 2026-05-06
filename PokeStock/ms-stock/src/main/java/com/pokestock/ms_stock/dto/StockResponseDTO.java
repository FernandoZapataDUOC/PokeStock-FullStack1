package com.pokestock.ms_stock.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDTO {

    private Long id;
    private Long productoId;
    private String lote;
    private Integer cantidad;
    private String ubicacion;
    private LocalDateTime fechaActualizacion;
}