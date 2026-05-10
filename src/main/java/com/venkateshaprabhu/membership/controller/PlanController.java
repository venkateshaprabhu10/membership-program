package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.dto.response.PlanResponse;
import com.venkateshaprabhu.membership.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        List<PlanResponse> plans = planService.getAllPlans();
        return ResponseEntity.ok(ApiResponse.ok("Plans retrieved successfully", plans));
    }
}