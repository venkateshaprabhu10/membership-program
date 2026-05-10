package com.venkateshaprabhu.membership.dto.response;

import java.util.List;

public record TierResponse(
        Long id,
        String name,
        int rank,
        String description,
        List<PerkResponse> perks
) {}