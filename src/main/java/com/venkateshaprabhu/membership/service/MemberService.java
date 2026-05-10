package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.dto.request.CreateMemberRequest;
import com.venkateshaprabhu.membership.dto.response.MemberResponse;
import com.venkateshaprabhu.membership.entity.Member;
import com.venkateshaprabhu.membership.entity.SubBrand;
import com.venkateshaprabhu.membership.exception.MembershipException;
import com.venkateshaprabhu.membership.exception.ResourceNotFoundException;
import com.venkateshaprabhu.membership.repository.MemberRepository;
import com.venkateshaprabhu.membership.repository.SubBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SubBrandRepository subBrandRepository;

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new MembershipException("A member with email '" + request.email() + "' already exists");
        }

        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        member.setPhone(request.phone());
        member.setJoinedAt(LocalDateTime.now());
        member.setActive(true);

        if (request.subBrandSlug() != null) {
            SubBrand subBrand = subBrandRepository.findBySlug(request.subBrandSlug())
                    .orElseThrow(() -> new ResourceNotFoundException("SubBrand not found with slug: " + request.subBrandSlug()));
            member.setSubBrand(subBrand);
        }

        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return toResponse(member);
    }

    public Member getMemberEntity(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getSubBrand() != null ? member.getSubBrand().getName() : null,
                member.getJoinedAt()
        );
    }
}