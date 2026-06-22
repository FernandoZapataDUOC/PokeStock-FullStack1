package com.pokestock.ms_security.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedTokenRequestDTO {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    private LocalDateTime expiresAt;
}
