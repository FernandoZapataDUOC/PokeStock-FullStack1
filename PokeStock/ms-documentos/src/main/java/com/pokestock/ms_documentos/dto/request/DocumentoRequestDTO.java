package com.pokestock.ms_documentos.dto.request;

import com.pokestock.ms_documentos.model.TipoDocumento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRequestDTO {

    @NotNull(message = "El movimiento es obligatorio")
    @Schema(description = "ID del movimiento de inventario asociado al documento", example = "1")
    private Long movimientoId;

    @NotNull(message = "El tipo de documento es obligatorio")
    @Schema(description = "Tipo de documento tributario de respaldo (ej. FACTURA, GUIA_DESPACHO)", example = "FACTURA")
    private TipoDocumento tipo;

    @NotBlank(message = "El archivo es obligatorio")
    @Size(max = 255)
    @Schema(description = "URL, ruta o nombre del archivo de respaldo digitalizado", example = "https://bucket.s3/factura-001.pdf")
    private String archivo;

    @Size(max = 500)
    @Schema(description = "Observaciones o glosas del documento", example = "Factura de compra autorizada por finanzas")
    private String observacion;
}