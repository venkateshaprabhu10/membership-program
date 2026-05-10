package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotNull Long memberId,
        @NotNull Long planId,
        @NotNull Long tierId
) {}