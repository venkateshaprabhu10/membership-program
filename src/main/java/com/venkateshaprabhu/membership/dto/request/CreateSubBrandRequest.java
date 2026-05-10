package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSubBrandRequest(
        @NotBlank String name,
        @NotBlank String slug,
        String description
) {}