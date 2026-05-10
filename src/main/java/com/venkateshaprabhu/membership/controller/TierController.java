package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.dto.response.TierResponse;
import com.venkateshaprabhu.membership.service.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiers")
@RequiredArgsConstructor
public class TierController {

    private final TierService tierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TierResponse>>> getAllTiers() {
        List<TierResponse> tiers = tierService.getAllTiers();
        return ResponseEntity.ok(ApiResponse.ok("Tiers retrieved successfully", tiers));
    }
}