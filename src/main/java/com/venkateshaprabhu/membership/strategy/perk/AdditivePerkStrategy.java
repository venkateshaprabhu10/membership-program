package com.venkateshaprabhu.membership.strategy.perk;

import com.venkateshaprabhu.membership.entity.PerkDefinition;
import com.venkateshaprabhu.membership.enums.PerkSource;
import com.venkateshaprabhu.membership.enums.PerkType;
import com.venkateshaprabhu.membership.enums.StackingStrategyType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class AdditivePerkStrategy implements PerkStackingStrategy {

    @Override
    public StackingStrategyType strategyType() {
        return StackingStrategyType.ADDITIVE;
    }

    @Override
    public List<SourcedPerk> merge(List<SourcedPerk> allPerks) {
        // Group by PerkType. For FREE_DELIVERY, take the min (additive summing of a threshold doesn't benefit the user).
        // For all other types, sum the numericValues to produce an aggregated virtual perk.
        Map<PerkType, List<SourcedPerk>> grouped = new LinkedHashMap<>();
        for (SourcedPerk sp : allPerks) {
            grouped.computeIfAbsent(sp.perk().getType(), k -> new ArrayList<>()).add(sp);
        }

        List<SourcedPerk> result = new ArrayList<>();
        for (Map.Entry<PerkType, List<SourcedPerk>> entry : grouped.entrySet()) {
            PerkType type = entry.getKey();
            List<SourcedPerk> group = entry.getValue();

            if (type == PerkType.FREE_DELIVERY) {
                // Lower threshold is better; pick min
                SourcedPerk best = group.stream()
                        .min(Comparator.comparing(sp -> sp.perk().getNumericValue()))
                        .orElseThrow();
                result.add(best);
            } else {
                // Sum numericValues; determine source (TIER if any TIER perk present, else COHORT)
                BigDecimal total = group.stream()
                        .map(sp -> sp.perk().getNumericValue())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                boolean hasTier = group.stream().anyMatch(sp -> sp.source() == PerkSource.TIER);
                PerkSource mergedSource = hasTier ? PerkSource.TIER : PerkSource.COHORT;

                // Use the first perk's definition as the base and override numericValue
                PerkDefinition base = group.get(0).perk();
                PerkDefinition aggregated = buildAggregated(base, total);
                result.add(new SourcedPerk(aggregated, mergedSource));
            }
        }

        return result;
    }

    // Builds a transient PerkDefinition (never persisted) to carry the summed numericValue in the response.
    private PerkDefinition buildAggregated(PerkDefinition base, BigDecimal totalValue) {
        PerkDefinition pd = new PerkDefinition();
        pd.setId(base.getId());
        pd.setName(base.getName());
        pd.setDescription(base.getDescription());
        pd.setType(base.getType());
        pd.setNumericValue(totalValue);
        pd.setConfigJson(base.getConfigJson());
        pd.setActive(base.isActive());
        return pd;
    }
}