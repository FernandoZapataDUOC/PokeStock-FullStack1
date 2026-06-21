package com.pokestock.ms_proveedores.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorResponseDTO {

    @Schema(description = "ID único auto-incremental del proveedor", example = "1")
    private Long id;

    @Schema(description = "Nombre oficial de la empresa del proveedor", example = "Devir Chile")
    private String nombre;

    @Schema(description = "Contacto comercial principal", example = "Sebastián Andrade")
    private String contacto;

    @Schema(description = "País de origen", example = "Chile")
    private String pais;

    @Schema(description = "Correo electrónico de contacto", example = "contacto@devir.cl")
    private String email;

    @Schema(description = "Estado de actividad del proveedor (borrado lógico)", example = "true")
    private Boolean activo;
}