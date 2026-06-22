package com.pokestock.ms_auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-security")
public interface SecurityFeignClient {

    @PostMapping("/api/security/audits")
    ResponseEntity<Map<String, Object>> registrarAuditoria(@RequestBody Map<String, Object> request);
}
