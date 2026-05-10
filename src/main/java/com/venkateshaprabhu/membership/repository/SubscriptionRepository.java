package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.Subscription;
import com.venkateshaprabhu.membership.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByMemberAndStatus(Member member, SubscriptionStatus status);
    boolean existsByMemberAndStatus(Member member, SubscriptionStatus status);
}