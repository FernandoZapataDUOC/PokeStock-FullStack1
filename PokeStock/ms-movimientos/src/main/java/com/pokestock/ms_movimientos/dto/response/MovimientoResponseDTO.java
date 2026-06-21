package com.pokestock.ms_movimientos.dto.response;

import com.pokestock.ms_movimientos.model.EstadoMovimiento;
import com.pokestock.ms_movimientos.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoResponseDTO {

    @Schema(description = "ID único del movimiento", example = "1")
    private Long id;

    @Schema(description = "ID del producto transaccionado", example = "1")
    private Long productoId;

    @Schema(description = "ID del proveedor asociado", example = "1")
    private Long proveedorId;

    @Schema(description = "Tipo de movimiento realizado", example = "ENTRADA")
    private TipoMovimiento tipo;

    @Schema(description = "Cantidad de unidades", example = "10")
    private Integer cantidad;

    @Schema(description = "Estado de ciclo de vida del movimiento", example = "PENDIENTE")
    private EstadoMovimiento estado;

    @Schema(description = "Observaciones", example = "Ingreso de stock correspondiente al lote A-1")
    private String observacion;

    @Schema(description = "Fecha de registro del movimiento", example = "2026-06-21T15:20:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha del último cambio de estado", example = "2026-06-21T15:22:00")
    private LocalDateTime fechaActualizacion;
}