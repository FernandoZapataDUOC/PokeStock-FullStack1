package com.pokestock.ms_usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la creación o actualización de un rol")
public class RolRequestDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Schema(description = "Nombre del rol de usuario en la plataforma", example = "ROLE_ADMIN")
    private String nombre;

    @Schema(description = "Descripción detallada de los permisos o propósito del rol", example = "Administrador del sistema con acceso total")
    private String descripcion;
}
