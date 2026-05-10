package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.NotNull;

public record AttachPerkRequest(
        @NotNull Long perkId,
        boolean enabled
) {}