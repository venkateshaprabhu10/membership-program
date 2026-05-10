package com.venkateshaprabhu.membership;

import com.venkateshaprabhu.membership.entity.*;
import com.venkateshaprabhu.membership.enums.CriteriaType;
import com.venkateshaprabhu.membership.enums.PerkType;
import com.venkateshaprabhu.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SubBrandRepository subBrandRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final PerkDefinitionRepository perkRepository;
    private final TierPerkRepository tierPerkRepository;
    private final TierCriteriaRepository criteriaRepository;
    private final CohortRepository cohortRepository;
    private final CohortPerkRepository cohortPerkRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // --- Sub-brands ---
        SubBrand fresh = createSubBrand("FirstClub Fresh", "fresh", "Fresh grocery sub-brand");
        SubBrand b2b = createSubBrand("FirstClub B2B", "b2b", "Business-to-business sub-brand");

        // --- Plans ---
        MembershipPlan planMonthly = createPlan("Fresh Monthly", "Monthly fresh membership", 30, new BigDecimal("149.00"), "INR");
        MembershipPlan planQuarterly = createPlan("Fresh Quarterly", "Quarterly fresh membership", 90, new BigDecimal("399.00"), "INR");
        MembershipPlan planAnnual = createPlan("Club Annual", "Annual club membership", 365, new BigDecimal("999.00"), "INR");

        // --- Perks ---
        PerkDefinition freeDelivery299 = createPerk("Free Delivery",
                "Free delivery on orders above ₹299",
                PerkType.FREE_DELIVERY, new BigDecimal("299"), "{\"minOrderAmount\": 299}");

        PerkDefinition freeDeliveryPremium = createPerk("Free Delivery Premium",
                "Always free delivery",
                PerkType.FREE_DELIVERY, new BigDecimal("0"), "{\"minOrderAmount\": 0}");

        PerkDefinition discount5 = createPerk("Fruits & Dairy Discount 5%",
                "5% off on fruits and dairy",
                PerkType.ITEM_DISCOUNT, new BigDecimal("5"), "{\"percentage\": 5, \"categories\": [\"fruits\", \"dairy\"]}");

        PerkDefinition discount10 = createPerk("Fruits & Dairy Discount 10%",
                "10% off on fruits, dairy and vegetables",
                PerkType.ITEM_DISCOUNT, new BigDecimal("10"), "{\"percentage\": 10, \"categories\": [\"fruits\", \"dairy\", \"vegetables\"]}");

        PerkDefinition discount15 = createPerk("All Products Discount 15%",
                "15% off on all products",
                PerkType.ITEM_DISCOUNT, new BigDecimal("15"), "{\"percentage\": 15, \"categories\": [\"all\"]}");

        PerkDefinition earlyAccess = createPerk("Exclusive Early Access",
                "24-hour early access to new products",
                PerkType.EXCLUSIVE_ACCESS, new BigDecimal("24"), "{\"earlyAccessHours\": 24}");

        PerkDefinition prioritySupport = createPerk("Priority Support",
                "Dedicated support line",
                PerkType.PRIORITY_SUPPORT, new BigDecimal("1"), "{\"channel\": \"dedicated_line\"}");

        PerkDefinition earlyAdopterBonus = createPerk("Early Adopter Bonus",
                "3% flat discount for early adopters",
                PerkType.FLAT_DISCOUNT, new BigDecimal("3"), "{\"percentage\": 3}");

        PerkDefinition b2bBulkDiscount = createPerk("B2B Bulk Discount",
                "8% bulk discount on all products for B2B buyers",
                PerkType.ITEM_DISCOUNT, new BigDecimal("8"), "{\"percentage\": 8, \"categories\": [\"all\"]}");

        // --- Tiers ---
        // Silver (rank=1): order count >= 1 in 30 days
        MembershipTier silver = createTier("Silver", 1, "Entry-level tier");
        addCriteria(silver, CriteriaType.ORDER_COUNT, "{\"threshold\": 1, \"periodDays\": 30}");
        addTierPerk(silver, freeDelivery299);
        addTierPerk(silver, discount5);

        // Gold (rank=2): order count >= 5 in 30 days OR order value >= 5000 in 30 days
        MembershipTier gold = createTier("Gold", 2, "Mid-level tier");
        addCriteria(gold, CriteriaType.ORDER_COUNT, "{\"threshold\": 5, \"periodDays\": 30}");
        addCriteria(gold, CriteriaType.ORDER_VALUE, "{\"threshold\": 5000.00, \"periodDays\": 30}");
        addTierPerk(gold, freeDeliveryPremium);
        addTierPerk(gold, discount10);
        addTierPerk(gold, earlyAccess);

        // Platinum (rank=3): order count >= 10 in 30 days OR order value >= 15000 in 30 days
        MembershipTier platinum = createTier("Platinum", 3, "Top-level tier");
        addCriteria(platinum, CriteriaType.ORDER_COUNT, "{\"threshold\": 10, \"periodDays\": 30}");
        addCriteria(platinum, CriteriaType.ORDER_VALUE, "{\"threshold\": 15000.00, \"periodDays\": 30}");
        addTierPerk(platinum, freeDeliveryPremium);
        addTierPerk(platinum, discount15);
        addTierPerk(platinum, earlyAccess);
        addTierPerk(platinum, prioritySupport);

        // --- Cohorts ---
        Cohort earlyAdopters = createCohort("Early Adopters", "Members who joined early", null);
        addCohortPerk(earlyAdopters, earlyAdopterBonus);

        Cohort b2bBuyers = createCohort("B2B Buyers", "Business buyers on the B2B sub-brand", b2b);
        addCohortPerk(b2bBuyers, b2bBulkDiscount);

        // --- Sample members ---
        createMember("Priya Sharma", "priya@example.com", "9876543210", null);
        createMember("Rahul Mehta", "rahul@example.com", "9876543211", b2b);

        log.info("FirstClub Membership System initialized: {} plans, {} tiers, {} perks seeded.",
                planRepository.count(), tierRepository.count(), perkRepository.count());
    }

    private SubBrand createSubBrand(String name, String slug, String description) {
        SubBrand sb = new SubBrand();
        sb.setName(name);
        sb.setSlug(slug);
        sb.setDescription(description);
        sb.setActive(true);
        return subBrandRepository.save(sb);
    }

    private MembershipPlan createPlan(String name, String description, int durationDays, BigDecimal price, String currency) {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(name);
        plan.setDescription(description);
        plan.setDurationDays(durationDays);
        plan.setPrice(price);
        plan.setCurrency(currency);
        plan.setActive(true);
        return planRepository.save(plan);
    }

    private PerkDefinition createPerk(String name, String description, PerkType type, BigDecimal numericValue, String configJson) {
        PerkDefinition perk = new PerkDefinition();
        perk.setName(name);
        perk.setDescription(description);
        perk.setType(type);
        perk.setNumericValue(numericValue);
        perk.setConfigJson(configJson);
        perk.setActive(true);
        return perkRepository.save(perk);
    }

    private MembershipTier createTier(String name, int rank, String description) {
        MembershipTier tier = new MembershipTier();
        tier.setName(name);
        tier.setRank(rank);
        tier.setDescription(description);
        tier.setActive(true);
        return tierRepository.save(tier);
    }

    private void addCriteria(MembershipTier tier, CriteriaType type, String configJson) {
        TierCriteria criteria = new TierCriteria();
        criteria.setTier(tier);
        criteria.setType(type);
        criteria.setConfigJson(configJson);
        criteriaRepository.save(criteria);
    }

    private void addTierPerk(MembershipTier tier, PerkDefinition perk) {
        TierPerk tierPerk = new TierPerk();
        tierPerk.setTier(tier);
        tierPerk.setPerk(perk);
        tierPerk.setEnabled(true);
        tierPerkRepository.save(tierPerk);
    }

    private Cohort createCohort(String name, String description, SubBrand subBrand) {
        Cohort cohort = new Cohort();
        cohort.setName(name);
        cohort.setDescription(description);
        cohort.setSubBrand(subBrand);
        cohort.setActive(true);
        return cohortRepository.save(cohort);
    }

    private void addCohortPerk(Cohort cohort, PerkDefinition perk) {
        CohortPerk cohortPerk = new CohortPerk();
        cohortPerk.setCohort(cohort);
        cohortPerk.setPerk(perk);
        cohortPerk.setEnabled(true);
        cohortPerkRepository.save(cohortPerk);
    }

    private void createMember(String name, String email, String phone, SubBrand subBrand) {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setPhone(phone);
        member.setSubBrand(subBrand);
        member.setJoinedAt(LocalDateTime.now());
        member.setActive(true);
        memberRepository.save(member);
    }
}
