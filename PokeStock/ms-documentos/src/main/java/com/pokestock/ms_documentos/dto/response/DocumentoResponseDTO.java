package com.pokestock.ms_documentos.dto.response;

import com.pokestock.ms_documentos.model.TipoDocumento;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseDTO {

    private Long id;
    private Long movimientoId;
    private TipoDocumento tipo;
    private String archivo;
    private Boolean validado;
    private String observacion;
}