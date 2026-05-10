package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.request.SubscribeRequest;
import com.venkateshaprabhu.membership.dto.request.TierChangeRequest;
import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.dto.response.EffectivePerksResponse;
import com.venkateshaprabhu.membership.dto.response.SubscriptionResponse;
import com.venkateshaprabhu.membership.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(@Valid @RequestBody SubscribeRequest request) {
        SubscriptionResponse response = subscriptionService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Subscription created successfully", response));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscription(@PathVariable Long memberId) {
        SubscriptionResponse response = subscriptionService.getSubscription(memberId);
        return ResponseEntity.ok(ApiResponse.ok("Subscription retrieved successfully", response));
    }

    @PutMapping("/{memberId}/tier")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> changeTier(
            @PathVariable Long memberId,
            @Valid @RequestBody TierChangeRequest request) {
        SubscriptionResponse response = subscriptionService.changeTier(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("Tier updated successfully", response));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancel(@PathVariable Long memberId) {
        SubscriptionResponse response = subscriptionService.cancel(memberId);
        return ResponseEntity.ok(ApiResponse.ok("Subscription cancelled successfully", response));
    }

    @GetMapping("/{memberId}/perks")
    public ResponseEntity<ApiResponse<EffectivePerksResponse>> getEffectivePerks(@PathVariable Long memberId) {
        EffectivePerksResponse response = subscriptionService.getEffectivePerks(memberId);
        return ResponseEntity.ok(ApiResponse.ok("Effective perks retrieved successfully", response));
    }
}
