package com.pokestock.ms_usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa la respuesta con los datos de un rol")
public class RolResponseDTO {

    @Schema(description = "ID único del rol registrado", example = "1")
    private Long id;

    @Schema(description = "Nombre único del rol", example = "ROLE_ADMIN")
    private String nombre;

    @Schema(description = "Descripción detallada del rol", example = "Administrador del sistema con acceso total")
    private String descripcion;
}
