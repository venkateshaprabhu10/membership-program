package com.venkateshaprabhu.membership.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderRecordedEvent extends ApplicationEvent {

    private final Long memberId;
    private final Long orderId;

    public OrderRecordedEvent(Object source, Long memberId, Long orderId) {
        super(source);
        this.memberId = memberId;
        this.orderId = orderId;
    }

}