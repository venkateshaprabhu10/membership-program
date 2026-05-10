package com.venkateshaprabhu.membership.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cohorts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // Optional: cohort may be scoped to a specific sub-brand
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_brand_id")
    private SubBrand subBrand;

    @Column(nullable = false)
    private boolean active = true;
}