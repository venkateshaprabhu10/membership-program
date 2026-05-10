package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.TierCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierCriteriaRepository extends JpaRepository<TierCriteria, Long> {
}