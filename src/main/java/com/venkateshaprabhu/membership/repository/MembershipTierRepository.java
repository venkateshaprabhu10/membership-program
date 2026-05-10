package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    List<MembershipTier> findByActiveTrueOrderByRankDesc();
    List<MembershipTier> findByActiveTrueOrderByRankAsc();
    Optional<MembershipTier> findByActiveTrueAndRank(int rank);
}