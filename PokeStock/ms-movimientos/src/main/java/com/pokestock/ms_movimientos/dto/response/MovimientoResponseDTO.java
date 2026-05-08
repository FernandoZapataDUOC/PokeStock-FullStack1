package com.pokestock.ms_movimientos.dto.response;

import com.pokestock.ms_movimientos.model.EstadoMovimiento;
import com.pokestock.ms_movimientos.model.TipoMovimiento;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoResponseDTO {

    private Long id;
    private Long productoId;
    private Long proveedorId;
    private TipoMovimiento tipo;
    private Integer cantidad;
    private EstadoMovimiento estado;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}