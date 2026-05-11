package com.pokestock.ms_movimientos.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockClientDTO {

    private Long id;

    @JsonProperty("productoId")
    private Long productoId;

    private Integer cantidad;

    private String lote;

    private String ubicacion;

    @JsonProperty("fechaActualizacion")
    private LocalDateTime fechaActualizacion;
}