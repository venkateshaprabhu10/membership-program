package com.venkateshaprabhu.membership.strategy.tier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.TierCriteria;
import com.venkateshaprabhu.membership.enums.CriteriaType;
import com.venkateshaprabhu.membership.repository.UserCohortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CohortCriteriaStrategy implements TierCriteriaStrategy {

    private final UserCohortRepository userCohortRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CriteriaType supportedType() {
        return CriteriaType.COHORT_MEMBERSHIP;
    }

    @Override
    public boolean evaluate(Member member, TierCriteria criteria) {
        try {
            JsonNode config = objectMapper.readTree(criteria.getConfigJson());
            long cohortId = config.get("cohortId").asLong();
            return userCohortRepository.existsByMemberIdAndCohortId(member.getId(), cohortId);
        } catch (Exception e) {
            log.warn("Failed to evaluate COHORT_MEMBERSHIP criteria id={}: {}", criteria.getId(), e.getMessage());
            return false;
        }
    }
}