package com.venkateshaprabhu.membership.dto.request;

import com.venkateshaprabhu.membership.enums.PerkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePerkRequest(
        @NotBlank String name,
        String description,
        @NotNull PerkType type,
        @NotNull BigDecimal numericValue,
        String configJson
) {}