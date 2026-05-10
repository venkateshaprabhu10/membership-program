package com.venkateshaprabhu.membership.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TierChangedEvent extends ApplicationEvent {

    private final Long memberId;
    private final String previousTierName;
    private final String newTierName;

    public TierChangedEvent(Object source, Long memberId, String previousTierName, String newTierName) {
        super(source);
        this.memberId = memberId;
        this.previousTierName = previousTierName;
        this.newTierName = newTierName;
    }

}