package com.venkateshaprabhu.membership.entity;

import com.venkateshaprabhu.membership.enums.PerkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "perk_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerkDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerkType type;

    // Primary comparable value: e.g. 10 for 10% discount, 299 for min-order threshold for free delivery
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal numericValue;

    // Full config JSON, e.g. {"percentage": 10, "categories": ["fruits", "dairy"]}
    private String configJson;

    @Column(nullable = false)
    private boolean active = true;
}