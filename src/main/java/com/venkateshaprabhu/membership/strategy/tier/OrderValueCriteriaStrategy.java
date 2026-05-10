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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValueCriteriaStrategy implements TierCriteriaStrategy {

    private final OrderRecordRepository orderRecordRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CriteriaType supportedType() {
        return CriteriaType.ORDER_VALUE;
    }

    @Override
    public boolean evaluate(Member member, TierCriteria criteria) {
        try {
            JsonNode config = objectMapper.readTree(criteria.getConfigJson());
            BigDecimal threshold = new BigDecimal(config.get("threshold").asText());
            int periodDays = config.get("periodDays").asInt();
            LocalDateTime since = LocalDateTime.now().minusDays(periodDays);
            BigDecimal totalAmount = orderRecordRepository.sumAmountByMemberAndOrderedAtAfter(member, since);
            return totalAmount.compareTo(threshold) >= 0;
        } catch (Exception e) {
            log.warn("Failed to evaluate ORDER_VALUE criteria id={}: {}", criteria.getId(), e.getMessage());
            return false;
        }
    }
}