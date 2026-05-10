package com.venkateshaprabhu.membership.config;

import com.venkateshaprabhu.membership.strategy.perk.AdditivePerkStrategy;
import com.venkateshaprabhu.membership.strategy.perk.PerkStackingStrategy;
import com.venkateshaprabhu.membership.strategy.perk.UnionMaxValuePerkStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PerkStrategyConfig {

    @Bean
    public PerkStackingStrategy perkStackingStrategy(
            BrandConfig brandConfig,
            UnionMaxValuePerkStrategy unionMaxValueStrategy,
            AdditivePerkStrategy additiveStrategy
    ) {
        return switch (brandConfig.getPerkStackingStrategy()) {
            case ADDITIVE -> additiveStrategy;
            default -> unionMaxValueStrategy;
        };
    }
}