package com.pokestock.ms_movimientos.client.dto;

import lombok.Data;

@Data
public class DocumentoClientDTO {
    private Long id;
    private Long movimientoId;
    private String tipo;
    private Boolean validado;
}