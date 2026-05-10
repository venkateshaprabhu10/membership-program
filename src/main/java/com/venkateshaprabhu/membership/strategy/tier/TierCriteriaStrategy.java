package com.venkateshaprabhu.membership.strategy.tier;

import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.TierCriteria;
import com.venkateshaprabhu.membership.enums.CriteriaType;

public interface TierCriteriaStrategy {
    CriteriaType supportedType();
    boolean evaluate(Member member, TierCriteria criteria);
}