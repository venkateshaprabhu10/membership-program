package com.venkateshaprabhu.membership.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String phone,
        String subBrandSlug
) {}