package com.pokestock.ms_documentos.dto.request;

import com.pokestock.ms_documentos.model.TipoDocumento;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRequestDTO {

    @NotNull(message = "El movimiento es obligatorio")
    private Long movimientoId;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipo;

    @NotBlank(message = "El archivo es obligatorio")
    @Size(max = 255)
    private String archivo;

    @Size(max = 500)
    private String observacion;
}