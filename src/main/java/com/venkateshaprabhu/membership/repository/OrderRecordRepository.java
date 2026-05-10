package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRecordRepository extends JpaRepository<OrderRecord, Long> {

    List<OrderRecord> findByMember(Member member);

    @Query("SELECT COUNT(o) FROM OrderRecord o WHERE o.member = :member AND o.orderedAt >= :since")
    long countByMemberAndOrderedAtAfter(@Param("member") Member member, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM OrderRecord o WHERE o.member = :member AND o.orderedAt >= :since")
    BigDecimal sumAmountByMemberAndOrderedAtAfter(@Param("member") Member member, @Param("since") LocalDateTime since);
}