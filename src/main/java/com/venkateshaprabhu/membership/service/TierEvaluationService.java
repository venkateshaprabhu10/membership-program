package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.MembershipTier;
import com.venkateshaprabhu.membership.entity.Subscription;
import com.venkateshaprabhu.membership.entity.TierCriteria;
import com.venkateshaprabhu.membership.enums.CriteriaType;
import com.venkateshaprabhu.membership.enums.SubscriptionStatus;
import com.venkateshaprabhu.membership.event.TierChangedEvent;
import com.venkateshaprabhu.membership.repository.MemberRepository;
import com.venkateshaprabhu.membership.repository.MembershipTierRepository;
import com.venkateshaprabhu.membership.repository.SubscriptionRepository;
import com.venkateshaprabhu.membership.strategy.tier.TierCriteriaStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TierEvaluationService {

    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MembershipTierRepository tierRepository;
    private final ApplicationEventPublisher eventPublisher;
    // Strategy map built from all TierCriteriaStrategy @Component beans
    private final Map<CriteriaType, TierCriteriaStrategy> strategyMap;

    public TierEvaluationService(
            MemberRepository memberRepository,
            SubscriptionRepository subscriptionRepository,
            MembershipTierRepository tierRepository,
            ApplicationEventPublisher eventPublisher,
            List<TierCriteriaStrategy> strategies
    ) {
        this.memberRepository = memberRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.tierRepository = tierRepository;
        this.eventPublisher = eventPublisher;

        this.strategyMap = new EnumMap<>(CriteriaType.class);
        for (TierCriteriaStrategy strategy : strategies) {
            this.strategyMap.put(strategy.supportedType(), strategy);
        }
    }

    @Transactional
    public void evaluateAndUpdate(Long memberId) {
        try {
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member == null) {
                log.warn("TierEvaluation: member {} not found, skipping", memberId);
                return;
            }

            Optional<Subscription> optSub = subscriptionRepository.findByMemberAndStatus(member, SubscriptionStatus.ACTIVE);
            if (optSub.isEmpty()) {
                log.debug("TierEvaluation: no active subscription for member {}, skipping", memberId);
                return;
            }

            Subscription subscription = optSub.get();

            // Evaluate tiers from highest rank down; the first whose ANY criterion passes becomes the target
            List<MembershipTier> tiersDescending = tierRepository.findByActiveTrueOrderByRankDesc();
            MembershipTier targetTier = null;
            for (MembershipTier tier : tiersDescending) {
                if (anyCriterionPasses(member, tier.getCriteria())) {
                    targetTier = tier;
                    break;
                }
            }

            // Default to the lowest tier (rank=1) if no tier criterion matched
            if (targetTier == null) {
                targetTier = tierRepository.findByActiveTrueAndRank(1)
                        .orElse(tiersDescending.isEmpty() ? null : tiersDescending.get(tiersDescending.size() - 1));
            }

            if (targetTier == null) {
                log.warn("TierEvaluation: no tiers configured, cannot evaluate member {}", memberId);
                return;
            }

            if (!targetTier.getId().equals(subscription.getTier().getId())) {
                String previousTierName = subscription.getTier().getName();
                subscription.setTier(targetTier);
                subscriptionRepository.save(subscription);
                eventPublisher.publishEvent(new TierChangedEvent(this, memberId, previousTierName, targetTier.getName()));
                log.info("TierEvaluation: member {} upgraded from {} to {}", memberId, previousTierName, targetTier.getName());
            }

        } catch (ObjectOptimisticLockingFailureException ex) {
            // Concurrent evaluation already updated the subscription correctly; the second write is redundant.
            log.warn("TierEvaluation: optimistic locking conflict for member {} — concurrent evaluation already handled it", memberId);
        }
    }

    private boolean anyCriterionPasses(Member member, List<TierCriteria> criteria) {
        for (TierCriteria criterion : criteria) {
            TierCriteriaStrategy strategy = strategyMap.get(criterion.getType());
            if (strategy != null && strategy.evaluate(member, criterion)) {
                return true;
            }
        }
        return false;
    }
}