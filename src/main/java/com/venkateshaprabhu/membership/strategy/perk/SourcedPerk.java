package com.venkateshaprabhu.membership.strategy.perk;

import com.venkateshaprabhu.membership.entity.PerkDefinition;
import com.venkateshaprabhu.membership.enums.PerkSource;

public record SourcedPerk(PerkDefinition perk, PerkSource source) {}
