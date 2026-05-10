package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCohortRequest(
        @NotBlank String name,
        String description,
        Long subBrandId
) {}