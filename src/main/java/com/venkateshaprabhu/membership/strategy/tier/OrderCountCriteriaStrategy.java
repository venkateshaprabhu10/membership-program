package com.venkateshaprabhu.membership.strategy.tier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.TierCriteria;
import com.venkateshaprabhu.membership.enums.CriteriaType;
import com.venkateshaprabhu.membership.repository.OrderRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCountCriteriaStrategy implements TierCriteriaStrategy {

    private final OrderRecordRepository orderRecordRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CriteriaType supportedType() {
        return CriteriaType.ORDER_COUNT;
    }

    @Override
    public boolean evaluate(Member member, TierCriteria criteria) {
        try {
            JsonNode config = objectMapper.readTree(criteria.getConfigJson());
            long threshold = config.get("threshold").asLong();
            int periodDays = config.get("periodDays").asInt();
            LocalDateTime since = LocalDateTime.now().minusDays(periodDays);
            long orderCount = orderRecordRepository.countByMemberAndOrderedAtAfter(member, since);
            return orderCount >= threshold;
        } catch (Exception e) {
            log.warn("Failed to evaluate ORDER_COUNT criteria id={}: {}", criteria.getId(), e.getMessage());
            return false;
        }
    }
}