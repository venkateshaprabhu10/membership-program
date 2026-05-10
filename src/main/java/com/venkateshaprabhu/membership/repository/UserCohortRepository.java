package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.UserCohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCohortRepository extends JpaRepository<UserCohort, Long> {
    List<UserCohort> findByMember(Member member);
    List<UserCohort> findByCohortId(Long cohortId);
    boolean existsByMemberIdAndCohortId(Long memberId, Long cohortId);
}