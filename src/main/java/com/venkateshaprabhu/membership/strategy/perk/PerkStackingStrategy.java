package com.venkateshaprabhu.membership.strategy.perk;

import com.venkateshaprabhu.membership.enums.StackingStrategyType;

import java.util.List;

public interface PerkStackingStrategy {
    List<SourcedPerk> merge(List<SourcedPerk> allPerks);
    StackingStrategyType strategyType();
}