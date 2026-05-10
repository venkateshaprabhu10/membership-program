package com.venkateshaprabhu.membership.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TierChangeAuditListener {

    @EventListener
    public void onTierChanged(TierChangedEvent event) {
        log.info("Member {} tier changed: {} -> {}", event.getMemberId(),
                event.getPreviousTierName(), event.getNewTierName());
    }
}