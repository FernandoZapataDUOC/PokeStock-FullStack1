package com.pokestock.ms_proveedores.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Schema(description = "Nombre oficial de la empresa o distribuidor del proveedor", example = "Devir Chile")
    private String nombre;

    @NotBlank(message = "El contacto es obligatorio")
    @Size(max = 100)
    @Schema(description = "Nombre del representante de ventas o contacto principal", example = "Sebastián Andrade")
    private String contacto;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 60)
    @Schema(description = "País de origen o locación principal del proveedor", example = "Chile")
    private String pais;

    @Email(message = "El email no tiene formato válido")
    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Dirección de correo electrónico de contacto comercial", example = "contacto@devir.cl")
    private String email;
}