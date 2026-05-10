package com.venkateshaprabhu.membership.dto.response;

import java.util.List;

public record EffectivePerksResponse(
        Long memberId,
        String memberName,
        String tierName,
        List<PerkResponse> effectivePerks
) {}