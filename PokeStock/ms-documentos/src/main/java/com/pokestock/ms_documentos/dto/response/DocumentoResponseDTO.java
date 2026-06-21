package com.pokestock.ms_documentos.dto.response;

import com.pokestock.ms_documentos.model.TipoDocumento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseDTO {

    @Schema(description = "ID único del documento", example = "1")
    private Long id;

    @Schema(description = "ID del movimiento asociado", example = "1")
    private Long movimientoId;

    @Schema(description = "Tipo de documento de respaldo", example = "FACTURA")
    private TipoDocumento tipo;

    @Schema(description = "Enlace o ubicación del archivo digitalizado", example = "https://bucket.s3/factura-001.pdf")
    private String archivo;

    @Schema(description = "Indica si el documento ha sido verificado y aprobado", example = "true")
    private Boolean validado;

    @Schema(description = "Observaciones de la auditoría", example = "Factura de compra autorizada por finanzas")
    private String observacion;
}