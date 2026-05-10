package com.venkateshaprabhu.membership.dto.response;

import com.venkateshaprabhu.membership.enums.PerkSource;
import com.venkateshaprabhu.membership.enums.PerkType;

import java.math.BigDecimal;

public record PerkResponse(
        Long id,
        String name,
        String description,
        PerkType type,
        BigDecimal numericValue,
        String configJson,
        PerkSource source
) {}