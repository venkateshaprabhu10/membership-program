package com.venkateshaprabhu.membership.entity;

import com.venkateshaprabhu.membership.enums.CriteriaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tier_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriteriaType type;

    // JSON config, e.g.:
    //   ORDER_COUNT: {"threshold": 5, "periodDays": 30}
    //   ORDER_VALUE: {"threshold": 5000.00, "periodDays": 30}
    //   COHORT_MEMBERSHIP: {"cohortId": 2}
    @Column(nullable = false, columnDefinition = "TEXT")
    private String configJson;
}