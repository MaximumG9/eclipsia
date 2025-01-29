package dev.osmii.shadow.enums

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

enum class ShadowTestAbilities(val constructor: KFunction<Ability>, val clazz : KClass<out Ability>) {
    ASSASSINATE(::KillOneNearby, KillOneNearby::class),
    POISONBURST(::SummonPoisonCloud, SummonPoisonCloud::class),
    CULL(::ScalingDamageAll, ScalingDamageAll::class),
    BEGONE(::TeleportRandomPlayer, TeleportRandomPlayer::class),
    TNT(::SpawnTntRandomPlayer, SpawnTntRandomPlayer::class),
    RANDOM(ShadowTestAbilities::getRandomAbility, KillOneNearby::class);

    fun getRandomAbility(): Ability {
        val value = ShadowTestAbilities.entries.random()
        return value.constructor.call()
    }

    companion object {
        fun getShadowSecondAbility(shadow: Shadow): Ability {
            return shadow.config.shadowAbility.
            constructor.call()
        }
    }
}
