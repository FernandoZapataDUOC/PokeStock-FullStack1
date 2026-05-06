package com.pokestock.ms_documentos.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidarDocumentoRequestDTO {

    // Al validar, se puede agregar una observación opcional
    private String observacion;
}