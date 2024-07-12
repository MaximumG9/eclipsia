package dev.osmii.shadow.config

import dev.osmii.shadow.enums.ShadowTestAbilities
import dev.osmii.shadow.game.abilities.Ability

object AbilityTestConfig {
    private var ability: ShadowTestAbilities = ShadowTestAbilities.ASASSINATE

    fun setAbility(testAbility: ShadowTestAbilities) {
        this.ability = testAbility
    }

    fun getShadowSecondAbility() : Ability {
        return ability.constructor.call()
    }
}