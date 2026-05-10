package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.dto.request.AddCohortMemberRequest;
import com.venkateshaprabhu.membership.dto.request.AttachPerkRequest;
import com.venkateshaprabhu.membership.dto.request.CreateCohortRequest;
import com.venkateshaprabhu.membership.dto.response.MemberResponse;
import com.venkateshaprabhu.membership.entity.Cohort;
import com.venkateshaprabhu.membership.entity.CohortPerk;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.PerkDefinition;
import com.venkateshaprabhu.membership.entity.SubBrand;
import com.venkateshaprabhu.membership.entity.UserCohort;
import com.venkateshaprabhu.membership.exception.MembershipException;
import com.venkateshaprabhu.membership.exception.ResourceNotFoundException;
import com.venkateshaprabhu.membership.repository.CohortPerkRepository;
import com.venkateshaprabhu.membership.repository.CohortRepository;
import com.venkateshaprabhu.membership.repository.MemberRepository;
import com.venkateshaprabhu.membership.repository.PerkDefinitionRepository;
import com.venkateshaprabhu.membership.repository.SubBrandRepository;
import com.venkateshaprabhu.membership.repository.UserCohortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CohortService {

    private final CohortRepository cohortRepository;
    private final CohortPerkRepository cohortPerkRepository;
    private final UserCohortRepository userCohortRepository;
    private final MemberRepository memberRepository;
    private final PerkDefinitionRepository perkDefinitionRepository;
    private final SubBrandRepository subBrandRepository;
    private final MemberService memberService;

    @Transactional
    public Cohort createCohort(CreateCohortRequest request) {
        Cohort cohort = new Cohort();
        cohort.setName(request.name());
        cohort.setDescription(request.description());
        cohort.setActive(true);

        if (request.subBrandId() != null) {
            SubBrand subBrand = subBrandRepository.findById(request.subBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubBrand not found with id: " + request.subBrandId()));
            cohort.setSubBrand(subBrand);
        }

        return cohortRepository.save(cohort);
    }

    @Transactional
    public UserCohort addMemberToCohort(Long cohortId, Long memberId) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with id: " + cohortId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        if (userCohortRepository.existsByMemberIdAndCohortId(memberId, cohortId)) {
            throw new MembershipException("Member " + memberId + " is already in cohort " + cohortId);
        }

        UserCohort userCohort = new UserCohort();
        userCohort.setMember(member);
        userCohort.setCohort(cohort);
        userCohort.setAssignedAt(LocalDateTime.now());
        return userCohortRepository.save(userCohort);
    }

    @Transactional
    public CohortPerk attachPerkToCohort(Long cohortId, AttachPerkRequest request) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with id: " + cohortId));
        PerkDefinition perk = perkDefinitionRepository.findById(request.perkId())
                .orElseThrow(() -> new ResourceNotFoundException("Perk not found with id: " + request.perkId()));

        CohortPerk cohortPerk = new CohortPerk();
        cohortPerk.setCohort(cohort);
        cohortPerk.setPerk(perk);
        cohortPerk.setEnabled(request.enabled());
        return cohortPerkRepository.save(cohortPerk);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getCohortMembers(Long cohortId) {
        if (!cohortRepository.existsById(cohortId)) {
            throw new ResourceNotFoundException("Cohort not found with id: " + cohortId);
        }
        return userCohortRepository.findByCohortId(cohortId).stream()
                .map(uc -> memberService.toResponse(uc.getMember()))
                .toList();
    }
}