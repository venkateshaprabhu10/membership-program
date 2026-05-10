package com.venkateshaprabhu.membership.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long memberId,
        String externalOrderId,
        BigDecimal amount,
        LocalDateTime orderedAt
) {}