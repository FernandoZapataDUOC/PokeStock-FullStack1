package com.pokestock.ms_security.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El token es obligatorio")
    @Column(nullable = false, length = 512, unique = true)
    private String token;

    @Builder.Default
    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Opcional, para permitir limpieza periódica
}
