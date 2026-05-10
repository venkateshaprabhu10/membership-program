package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.dto.request.OrderRequest;
import com.venkateshaprabhu.membership.dto.response.OrderResponse;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.OrderRecord;
import com.venkateshaprabhu.membership.entity.SubBrand;
import com.venkateshaprabhu.membership.event.OrderRecordedEvent;
import com.venkateshaprabhu.membership.exception.ResourceNotFoundException;
import com.venkateshaprabhu.membership.repository.MemberRepository;
import com.venkateshaprabhu.membership.repository.OrderRecordRepository;
import com.venkateshaprabhu.membership.repository.SubBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRecordRepository orderRecordRepository;
    private final MemberRepository memberRepository;
    private final SubBrandRepository subBrandRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse recordOrder(OrderRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.memberId()));

        OrderRecord order = new OrderRecord();
        order.setMember(member);
        order.setExternalOrderId(request.externalOrderId());
        order.setAmount(request.amount());
        order.setOrderedAt(LocalDateTime.now());

        if (request.subBrandSlug() != null) {
            SubBrand subBrand = subBrandRepository.findBySlug(request.subBrandSlug())
                    .orElseThrow(() -> new ResourceNotFoundException("SubBrand not found with slug: " + request.subBrandSlug()));
            order.setSubBrand(subBrand);
        }

        OrderRecord saved = orderRecordRepository.save(order);

        // Publish event after save — TierEvaluationListener handles async re-evaluation
        eventPublisher.publishEvent(new OrderRecordedEvent(this, member.getId(), saved.getId()));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
        return orderRecordRepository.findByMember(member).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(OrderRecord order) {
        return new OrderResponse(
                order.getId(),
                order.getMember().getId(),
                order.getExternalOrderId(),
                order.getAmount(),
                order.getOrderedAt()
        );
    }
}