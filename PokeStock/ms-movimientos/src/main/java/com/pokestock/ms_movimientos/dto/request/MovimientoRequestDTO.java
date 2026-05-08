package com.pokestock.ms_movimientos.dto.request;

import com.pokestock.ms_movimientos.model.TipoMovimiento;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovimientoRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @Size(max = 500)
    private String observacion;
}