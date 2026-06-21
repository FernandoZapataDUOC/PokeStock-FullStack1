package com.pokestock.ms_documentos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidarDocumentoRequestDTO {

    @Schema(description = "Observaciones opcionales ingresadas por el operador durante la auditoría", example = "Verificado con firma digital del SII")
    private String observacion;
}