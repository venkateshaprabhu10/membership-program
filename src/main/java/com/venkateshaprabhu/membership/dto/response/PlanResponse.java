package com.venkateshaprabhu.membership.dto.response;

import java.math.BigDecimal;

public record PlanResponse(
        Long id,
        String name,
        String description,
        int durationDays,
        BigDecimal price,
        String currency
) {}