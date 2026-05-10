package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.request.CreateSubBrandRequest;
import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.entity.SubBrand;
import com.venkateshaprabhu.membership.exception.MembershipException;
import com.venkateshaprabhu.membership.repository.SubBrandRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sub-brands")
@RequiredArgsConstructor
public class AdminSubBrandController {

    private final SubBrandRepository subBrandRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<SubBrand>> createSubBrand(@Valid @RequestBody CreateSubBrandRequest request) {
        if (subBrandRepository.findBySlug(request.slug()).isPresent()) {
            throw new MembershipException("SubBrand with slug '" + request.slug() + "' already exists");
        }
        SubBrand subBrand = new SubBrand();
        subBrand.setName(request.name());
        subBrand.setSlug(request.slug());
        subBrand.setDescription(request.description());
        subBrand.setActive(true);
        SubBrand saved = subBrandRepository.save(subBrand);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("SubBrand created successfully", saved));
    }
}
