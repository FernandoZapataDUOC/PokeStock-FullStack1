package com.pokestock.ms_movimientos.dto.request;

import com.pokestock.ms_movimientos.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovimientoRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto involucrado en la transacción", example = "1")
    private Long productoId;

    @NotNull(message = "El proveedor es obligatorio")
    @Schema(description = "ID del proveedor asociado a la transacción", example = "1")
    private Long proveedorId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Schema(description = "Tipo de movimiento de stock", example = "ENTRADA")
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad de unidades a transaccionar", example = "10")
    private Integer cantidad;

    @Size(max = 500)
    @Schema(description = "Observaciones o justificación del movimiento", example = "Ingreso de stock correspondiente al lote A-1")
    private String observacion;
}