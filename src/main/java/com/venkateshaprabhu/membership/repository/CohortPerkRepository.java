package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.Cohort;
import com.venkateshaprabhu.membership.entity.CohortPerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CohortPerkRepository extends JpaRepository<CohortPerk, Long> {
    List<CohortPerk> findByCohortAndEnabledTrue(Cohort cohort);
}