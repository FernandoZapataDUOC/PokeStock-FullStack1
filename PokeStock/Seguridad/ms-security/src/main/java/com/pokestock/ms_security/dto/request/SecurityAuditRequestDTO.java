package com.pokestock.ms_security.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAuditRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La acción es obligatoria")
    private String action;

    private String ipAddress;
    private String details;
}
