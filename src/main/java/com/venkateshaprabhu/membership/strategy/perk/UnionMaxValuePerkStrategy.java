package com.venkateshaprabhu.membership.strategy.perk;

import com.venkateshaprabhu.membership.entity.PerkDefinition;
import com.venkateshaprabhu.membership.enums.PerkSource;
import com.venkateshaprabhu.membership.enums.PerkType;
import com.venkateshaprabhu.membership.enums.StackingStrategyType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UnionMaxValuePerkStrategy implements PerkStackingStrategy {

    @Override
    public StackingStrategyType strategyType() {
        return StackingStrategyType.UNION_MAX_VALUE;
    }

    @Override
    public List<SourcedPerk> merge(List<SourcedPerk> allPerks) {
        // Group by PerkType, then pick the "best" perk per type.
        // For FREE_DELIVERY, lower numericValue is better (lower min-order threshold = free delivery kicks in sooner).
        // For all other types, higher numericValue is better (bigger discount, more hours early access, etc.).
        Map<PerkType, SourcedPerk> bestByType = new LinkedHashMap<>();

        for (SourcedPerk sourcedPerk : allPerks) {
            PerkDefinition candidate = sourcedPerk.perk();
            PerkType type = candidate.getType();

            SourcedPerk current = bestByType.get(type);
            if (current == null) {
                bestByType.put(type, sourcedPerk);
            } else {
                if (isBetter(candidate, current.perk(), type)) {
                    bestByType.put(type, sourcedPerk);
                }
            }
        }

        return new ArrayList<>(bestByType.values());
    }

    private boolean isBetter(PerkDefinition candidate, PerkDefinition current, PerkType type) {
        int cmp = candidate.getNumericValue().compareTo(current.getNumericValue());
        // FREE_DELIVERY: lower threshold = better for user
        return (type == PerkType.FREE_DELIVERY) ? cmp < 0 : cmp > 0;
    }
}