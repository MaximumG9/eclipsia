package dev.osmii.shadow.enums

import dev.osmii.shadow.config.AbilityTestConfig
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

enum class ShadowTestAbilities(val constructor: KFunction<Ability>, val clazz : KClass<out Ability>) {
    ASASSINATE(::KillOneNearby, KillOneNearby::class),
    POISONBURST(::SummonPoisonCloud, SummonPoisonCloud::class),
    CULL(::ScalingDamageAll, ScalingDamageAll::class),
    BEGONE(::TeleportRandomPlayer, TeleportRandomPlayer::class),
    TNT(::SpawnTntRandomPlayer, SpawnTntRandomPlayer::class),
    RANDOM(AbilityTestConfig::getRandomValue, KillOneNearby::class);
}
