package com.venkateshaprabhu.membership.config;

import com.venkateshaprabhu.membership.enums.StackingStrategyType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// Deploy-per-brand: one running instance serves one brand. Switch brands by changing application.properties.
@Configuration
@ConfigurationProperties(prefix = "brand")
@Data
public class BrandConfig {
    private String name;
    private String slug;
    private String currency;
    private StackingStrategyType perkStackingStrategy;
}