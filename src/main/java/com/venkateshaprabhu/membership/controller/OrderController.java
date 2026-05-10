package com.venkateshaprabhu.membership.controller;

import com.venkateshaprabhu.membership.dto.request.OrderRequest;
import com.venkateshaprabhu.membership.dto.response.ApiResponse;
import com.venkateshaprabhu.membership.dto.response.OrderResponse;
import com.venkateshaprabhu.membership.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> recordOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.recordOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order recorded successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersForMember(@PathVariable Long memberId) {
        List<OrderResponse> orders = orderService.getOrdersForMember(memberId);
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved successfully", orders));
    }
}
