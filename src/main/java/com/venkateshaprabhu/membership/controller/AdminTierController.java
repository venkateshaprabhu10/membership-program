package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.request.AttachPerkRequest;
import com.venkateshaprabhu.membership.dto.request.CreatePerkRequest;
import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.dto.response.PerkResponse;
import com.venkateshaprabhu.membership.dto.response.TierResponse;
import com.venkateshaprabhu.membership.service.TierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tiers")
@RequiredArgsConstructor
public class AdminTierController {

    private final TierService tierService;

    @PostMapping("/{tierId}/perks")
    public ResponseEntity<ApiResponse<TierResponse>> attachPerkToTier(
            @PathVariable Long tierId,
            @Valid @RequestBody AttachPerkRequest request) {
        TierResponse response = tierService.attachPerkToTier(tierId, request);
        return ResponseEntity.ok(ApiResponse.ok("Perk attached to tier successfully", response));
    }

    @PostMapping("/perks")
    public ResponseEntity<ApiResponse<PerkResponse>> createPerk(@Valid @RequestBody CreatePerkRequest request) {
        PerkResponse response = tierService.createPerk(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Perk created successfully", response));
    }
}
