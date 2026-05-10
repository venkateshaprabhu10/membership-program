package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.dto.request.SubscribeRequest;
import com.venkateshaprabhu.membership.dto.request.TierChangeRequest;
import com.venkateshaprabhu.membership.dto.response.EffectivePerksResponse;
import com.venkateshaprabhu.membership.dto.response.SubscriptionResponse;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.MembershipPlan;
import com.venkateshaprabhu.membership.entity.MembershipTier;
import com.venkateshaprabhu.membership.entity.Subscription;
import com.venkateshaprabhu.membership.enums.SubscriptionStatus;
import com.venkateshaprabhu.membership.exception.MembershipException;
import com.venkateshaprabhu.membership.exception.ResourceNotFoundException;
import com.venkateshaprabhu.membership.repository.MemberRepository;
import com.venkateshaprabhu.membership.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PlanService planService;
    private final TierService tierService;
    private final PerkResolutionService perkResolutionService;

    @Transactional
    public SubscriptionResponse subscribe(SubscribeRequest request) {
        Member member = memberService.getMemberEntity(request.memberId());
        MembershipPlan plan = planService.getPlanEntity(request.planId());
        MembershipTier tier = tierService.getTierEntity(request.tierId());

        if (subscriptionRepository.existsByMemberAndStatus(member, SubscriptionStatus.ACTIVE)) {
            throw new MembershipException("Member " + request.memberId() + " already has an active subscription");
        }

        Subscription subscription = new Subscription();
        subscription.setMember(member);
        subscription.setPlan(plan);
        subscription.setTier(tier);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setExpiryDate(LocalDateTime.now().plusDays(plan.getDurationDays()));

        Subscription saved = subscriptionRepository.save(subscription);
        return toResponse(saved);
    }

    @Transactional
    public SubscriptionResponse changeTier(Long memberId, TierChangeRequest request) {
        Member member = memberService.getMemberEntity(memberId);
        MembershipTier newTier = tierService.getTierEntity(request.tierId());

        Subscription subscription = subscriptionRepository.findByMemberAndStatus(member, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for member: " + memberId));

        subscription.setTier(newTier);
        Subscription saved = subscriptionRepository.save(subscription);
        return toResponse(saved);
    }

    @Transactional
    public SubscriptionResponse cancel(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);
        Subscription subscription = subscriptionRepository.findByMemberAndStatus(member, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for member: " + memberId));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        Subscription saved = subscriptionRepository.save(subscription);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscription(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);
        Subscription subscription = subscriptionRepository.findByMemberAndStatus(member, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for member: " + memberId));
        return toResponse(subscription);
    }

    @Transactional(readOnly = true)
    public EffectivePerksResponse getEffectivePerks(Long memberId) {
        return perkResolutionService.getEffectivePerks(memberId);
    }

    private SubscriptionResponse toResponse(Subscription sub) {
        return new SubscriptionResponse(
                sub.getId(),
                sub.getMember().getId(),
                sub.getMember().getName(),
                sub.getPlan().getName(),
                sub.getTier().getName(),
                sub.getStatus(),
                sub.getStartDate(),
                sub.getExpiryDate()
        );
    }
}