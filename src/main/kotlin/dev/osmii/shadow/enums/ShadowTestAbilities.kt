package dev.osmii.shadow.enums

import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.*
import kotlin.reflect.KFunction

enum class ShadowTestAbilities(val constructor: KFunction<Ability>) {
    ASASSINATE(::KillOneNearby),
    POISONBURST(::PoisonCloud),
    KRILL(::ScalingDamageAll),
    BEGONE(::TeleportRandomPlayer),
    TNT(::SpawnTntRandomPlayer),
    RANDOM(ShadowTestAbilities::getRandomValue);

    fun getRandomValue() : Ability {
        val value = ShadowTestAbilities.entries.random()
        return value.constructor.call()
    }
}