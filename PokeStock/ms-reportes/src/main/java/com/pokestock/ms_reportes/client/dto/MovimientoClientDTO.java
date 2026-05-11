package com.pokestock.ms_reportes.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovimientoClientDTO {
    private Long id;
    private Long productoId;
    private Long proveedorId;
    private String tipo;
    private Integer cantidad;
    private String estado;
    private LocalDateTime fechaCreacion;
}