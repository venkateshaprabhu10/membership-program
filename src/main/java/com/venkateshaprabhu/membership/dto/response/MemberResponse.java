package com.venkateshaprabhu.membership.dto.response;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String name,
        String email,
        String phone,
        String subBrandName,
        LocalDateTime joinedAt
) {}