package com.venkateshaprabhu.membership.service;

import com.venkateshaprabhu.membership.dto.request.AttachPerkRequest;
import com.venkateshaprabhu.membership.dto.request.CreatePerkRequest;
import com.venkateshaprabhu.membership.dto.response.PerkResponse;
import com.venkateshaprabhu.membership.dto.response.TierResponse;
import com.venkateshaprabhu.membership.entity.MembershipTier;
import com.venkateshaprabhu.membership.entity.PerkDefinition;
import com.venkateshaprabhu.membership.entity.TierPerk;
import com.venkateshaprabhu.membership.enums.PerkSource;
import com.venkateshaprabhu.membership.exception.ResourceNotFoundException;
import com.venkateshaprabhu.membership.repository.MembershipTierRepository;
import com.venkateshaprabhu.membership.repository.PerkDefinitionRepository;
import com.venkateshaprabhu.membership.repository.TierPerkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierService {

    private final MembershipTierRepository tierRepository;
    private final PerkDefinitionRepository perkDefinitionRepository;
    private final TierPerkRepository tierPerkRepository;

    @Transactional(readOnly = true)
    public List<TierResponse> getAllTiers() {
        return tierRepository.findByActiveTrueOrderByRankAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TierResponse getTierById(Long id) {
        MembershipTier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with id: " + id));
        return toResponse(tier);
    }

    @Transactional
    public TierResponse attachPerkToTier(Long tierId, AttachPerkRequest request) {
        MembershipTier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with id: " + tierId));
        PerkDefinition perk = perkDefinitionRepository.findById(request.perkId())
                .orElseThrow(() -> new ResourceNotFoundException("Perk not found with id: " + request.perkId()));

        TierPerk tierPerk = new TierPerk();
        tierPerk.setTier(tier);
        tierPerk.setPerk(perk);
        tierPerk.setEnabled(request.enabled());
        tierPerkRepository.save(tierPerk);

        // Re-fetch to include updated perks list
        MembershipTier refreshed = tierRepository.findById(tierId).orElseThrow();
        return toResponse(refreshed);
    }

    @Transactional
    public PerkResponse createPerk(CreatePerkRequest request) {
        PerkDefinition perk = new PerkDefinition();
        perk.setName(request.name());
        perk.setDescription(request.description());
        perk.setType(request.type());
        perk.setNumericValue(request.numericValue());
        perk.setConfigJson(request.configJson());
        perk.setActive(true);
        PerkDefinition saved = perkDefinitionRepository.save(perk);
        return toPerkResponse(saved, PerkSource.TIER);
    }

    public MembershipTier getTierEntity(Long id) {
        return tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with id: " + id));
    }

    public TierResponse toResponse(MembershipTier tier) {
        List<PerkResponse> perks = tier.getTierPerks().stream()
                .filter(TierPerk::isEnabled)
                .map(tp -> toPerkResponse(tp.getPerk(), PerkSource.TIER))
                .toList();
        return new TierResponse(tier.getId(), tier.getName(), tier.getRank(), tier.getDescription(), perks);
    }

    public PerkResponse toPerkResponse(PerkDefinition perk, PerkSource source) {
        return new PerkResponse(
                perk.getId(),
                perk.getName(),
                perk.getDescription(),
                perk.getType(),
                perk.getNumericValue(),
                perk.getConfigJson(),
                source
        );
    }
}