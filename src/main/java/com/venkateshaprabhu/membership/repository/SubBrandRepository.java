package com.venkateshaprabhu.membership.repository;

import com.venkateshaprabhu.membership.entity.SubBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubBrandRepository extends JpaRepository<SubBrand, Long> {
    Optional<SubBrand> findBySlug(String slug);
}