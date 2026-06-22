package com.pokestock.ms_security.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank(message = "La acción es obligatoria")
    @Column(nullable = false, length = 50)
    private String action; // e.g., "LOGIN_SUCCESS", "LOGIN_FAILURE", "LOGOUT", "USER_REGISTERED"

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(length = 255)
    private String details;
}
