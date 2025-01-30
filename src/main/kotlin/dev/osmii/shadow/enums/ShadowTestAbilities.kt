package dev.osmii.shadow.enums

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.*
import kotlin.reflect.KClass

enum class ShadowTestAbilities(val constructor: (shadow : Shadow) -> Ability, val clazz : KClass<out Ability>) {
    ASSASSINATE({shadow -> KillOneNearby(shadow)}, KillOneNearby::class),
    POISONBURST({shadow -> SummonPoisonCloud(shadow)}, SummonPoisonCloud::class),
    CULL({shadow -> ScalingDamageAll(shadow)}, ScalingDamageAll::class),
    BEGONE({shadow -> TeleportRandomPlayer(shadow)}, TeleportRandomPlayer::class),
    TNT({shadow -> SpawnTntRandomPlayer(shadow)}, SpawnTntRandomPlayer::class),
    RANDOM({shadow -> ThisisActuallyInsane.getRandomAbility(shadow)}, KillOneNearby::class);

    companion object {
        fun getShadowSecondAbility(shadow: Shadow): Ability {
            return shadow.config.shadowAbility.constructor.invoke(shadow)
        }
    }

    object ThisisActuallyInsane {
        fun getRandomAbility(shadow: Shadow): Ability {
            val value = ShadowTestAbilities.entries.random()
            return value.constructor.invoke(shadow)
        }
    }
}
