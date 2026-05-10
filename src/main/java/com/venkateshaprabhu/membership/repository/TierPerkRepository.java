package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.MembershipTier;
import com.venkateshaprabhu.membership.entity.TierPerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierPerkRepository extends JpaRepository<TierPerk, Long> {
    List<TierPerk> findByTierAndEnabledTrue(MembershipTier tier);
}