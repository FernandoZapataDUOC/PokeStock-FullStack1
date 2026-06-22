package com.pokestock.ms_security.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedTokenResponseDTO {
    private Long id;
    private String token;
    private LocalDateTime blacklistedAt;
    private LocalDateTime expiresAt;
}
