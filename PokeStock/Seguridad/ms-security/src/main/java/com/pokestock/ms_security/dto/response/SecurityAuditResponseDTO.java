package com.pokestock.ms_security.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAuditResponseDTO {
    private Long id;
    private String username;
    private String action;
    private String ipAddress;
    private LocalDateTime timestamp;
    private String details;
}
