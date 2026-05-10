package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.PerkDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerkDefinitionRepository extends JpaRepository<PerkDefinition, Long> {
}