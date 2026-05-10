package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull Long memberId,
        @NotBlank String externalOrderId,
        @NotNull @Positive BigDecimal amount,
        String subBrandSlug
) {}