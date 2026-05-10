package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddCohortMemberRequest(
        @NotNull Long memberId
) {}