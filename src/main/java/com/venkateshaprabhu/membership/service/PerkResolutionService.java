package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.dto.response.EffectivePerksResponse;
import com.venkateshaprabhu.membership.dto.response.PerkResponse;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.Subscription;
import com.venkateshaprabhu.membership.entity.UserCohort;
import com.venkateshaprabhu.membership.enums.PerkSource;
import com.venkateshaprabhu.membership.enums.SubscriptionStatus;
import com.venkateshaprabhu.membership.exception.ResourceNotFoundException;
import com.venkateshaprabhu.membership.repository.CohortPerkRepository;
import com.venkateshaprabhu.membership.repository.MemberRepository;
import com.venkateshaprabhu.membership.repository.SubscriptionRepository;
import com.venkateshaprabhu.membership.repository.TierPerkRepository;
import com.venkateshaprabhu.membership.repository.UserCohortRepository;
import com.venkateshaprabhu.membership.strategy.perk.PerkStackingStrategy;
import com.venkateshaprabhu.membership.strategy.perk.SourcedPerk;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerkResolutionService {

    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TierPerkRepository tierPerkRepository;
    private final UserCohortRepository userCohortRepository;
    private final CohortPerkRepository cohortPerkRepository;
    private final PerkStackingStrategy perkStackingStrategy;
    private final TierService tierService;

    @Transactional(readOnly = true)
    public EffectivePerksResponse getEffectivePerks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Subscription subscription = subscriptionRepository.findByMemberAndStatus(member, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for member: " + memberId));

        // 1. Collect tier perks
        List<SourcedPerk> allPerks = new ArrayList<>(
                tierPerkRepository.findByTierAndEnabledTrue(subscription.getTier()).stream()
                        .map(tp -> new SourcedPerk(tp.getPerk(), PerkSource.TIER))
                        .toList()
        );

        // 2. Collect cohort perks from all cohorts the member belongs to
        List<UserCohort> userCohorts = userCohortRepository.findByMember(member);
        for (UserCohort userCohort : userCohorts) {
            cohortPerkRepository.findByCohortAndEnabledTrue(userCohort.getCohort()).stream()
                    .map(cp -> new SourcedPerk(cp.getPerk(), PerkSource.COHORT))
                    .forEach(allPerks::add);
        }

        // 3. Merge through configured stacking strategy
        List<SourcedPerk> merged = perkStackingStrategy.merge(allPerks);

        List<PerkResponse> perkResponses = merged.stream()
                .map(sp -> tierService.toPerkResponse(sp.perk(), sp.source()))
                .toList();

        return new EffectivePerksResponse(
                memberId,
                member.getName(),
                subscription.getTier().getName(),
                perkResponses
        );
    }
}