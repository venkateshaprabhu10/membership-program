package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.config.BrandConfig;
import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final BrandConfig brandConfig;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/api/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> info = Map.of(
                "status", "UP",
                "brand", brandConfig.getName(),
                "currency", brandConfig.getCurrency(),
                "perkStrategy", brandConfig.getPerkStackingStrategy().name()
        );
        return ResponseEntity.ok(ApiResponse.ok("Service is running", info));
    }
}
