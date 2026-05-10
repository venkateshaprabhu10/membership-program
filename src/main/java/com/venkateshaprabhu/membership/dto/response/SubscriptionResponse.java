package com.venkateshaprabhu.membership.dto.response;

import com.venkateshaprabhu.membership.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        Long memberId,
        String memberName,
        String planName,
        String tierName,
        SubscriptionStatus status,
        LocalDateTime startDate,
        LocalDateTime expiryDate
) {}