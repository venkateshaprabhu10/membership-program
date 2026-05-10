package com.venkateshaprabhu.membership.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Silver=1, Gold=2, Platinum=3 — higher rank = better tier
    @Column(nullable = false)
    private int rank;

    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "tier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TierCriteria> criteria = new ArrayList<>();

    @OneToMany(mappedBy = "tier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TierPerk> tierPerks = new ArrayList<>();
}