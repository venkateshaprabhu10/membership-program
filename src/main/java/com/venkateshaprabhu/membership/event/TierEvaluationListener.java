package com.venkateshaprabhu.membership.event;

import com.venkateshaprabhu.membership.service.TierEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationListener {
    private final TierEvaluationService tierEvaluationService;

    @Async("tierEvaluationExecutor")
    @EventListener
    public void onOrderRecorded(OrderRecordedEvent event) {
        log.debug("TierEvaluationListener: received OrderRecordedEvent for member {}, order {}",
                event.getMemberId(), event.getOrderId());
        tierEvaluationService.evaluateAndUpdate(event.getMemberId());
    }
}