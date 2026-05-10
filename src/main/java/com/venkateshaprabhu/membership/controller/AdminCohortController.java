package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.request.AddCohortMemberRequest;
import com.venkateshaprabhu.membership.dto.request.AttachPerkRequest;
import com.venkateshaprabhu.membership.dto.request.CreateCohortRequest;
import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.dto.response.MemberResponse;
import com.venkateshaprabhu.membership.entity.Cohort;
import com.venkateshaprabhu.membership.service.CohortService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cohorts")
@RequiredArgsConstructor
public class AdminCohortController {

    private final CohortService cohortService;

    @PostMapping
    public ResponseEntity<ApiResponse<Cohort>> createCohort(@Valid @RequestBody CreateCohortRequest request) {
        Cohort cohort = cohortService.createCohort(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cohort created successfully", cohort));
    }

    @PostMapping("/{cohortId}/members")
    public ResponseEntity<ApiResponse<Void>> addMemberToCohort(
            @PathVariable Long cohortId,
            @Valid @RequestBody AddCohortMemberRequest request) {
        cohortService.addMemberToCohort(cohortId, request.memberId());
        return ResponseEntity.ok(ApiResponse.ok("Member added to cohort successfully", null));
    }

    @PostMapping("/{cohortId}/perks")
    public ResponseEntity<ApiResponse<Void>> attachPerkToCohort(
            @PathVariable Long cohortId,
            @Valid @RequestBody AttachPerkRequest request) {
        cohortService.attachPerkToCohort(cohortId, request);
        return ResponseEntity.ok(ApiResponse.ok("Perk attached to cohort successfully", null));
    }

    @GetMapping("/{cohortId}/members")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getCohortMembers(@PathVariable Long cohortId) {
        List<MemberResponse> members = cohortService.getCohortMembers(cohortId);
        return ResponseEntity.ok(ApiResponse.ok("Cohort members retrieved successfully", members));
    }
}
