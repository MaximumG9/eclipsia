package dev.osmii.shadow.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg
import com.mojang.brigadier.arguments.DoubleArgumentType.getDouble
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.commands.ShadowTestAbilityArgument.Companion.getAbility
import dev.osmii.shadow.commands.ShadowTestAbilityArgument.Companion.testAbility
import dev.osmii.shadow.config.AbilityTestConfig
import dev.osmii.shadow.game.abilities.shadow.SpawnTntRandomPlayer
import dev.osmii.shadow.game.abilities.shadow.SummonPoisonCloud
import dev.osmii.shadow.game.abilities.shadow.TeleportRandomPlayer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal

class CommandConfig(val shadow: Shadow) {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("\$config")
                .then(
                    literal("shadowAbility")
                        .then(
                            argument("Ability",testAbility())
                        ).executes { context ->
                            val ability = getAbility(context,"Ability")
                            AbilityTestConfig.setAbility(ability)
                            return@executes 0
                        }
                )
                .then(
                    literal("poisonCloudRadius")
                        .then(
                            argument("Radius",doubleArg(0.0))
                        ).executes { context ->
                            val radius = getDouble(context,"Radius")
                            SummonPoisonCloud.RADIUS = radius
                            radius.toInt()
                        }
                )
                .then(
                    literal("poisonCloudDuration")
                        .then(
                            argument("Duration in ticks", integer(0))
                        ).executes { context ->
                            val duration = getInteger(context,"Duration in ticks")
                            SummonPoisonCloud.LIFETIME = duration
                            duration
                        }
                )
                .then(
                    literal("poisonCloudParticlesPerTick")
                        .then(
                            argument("Particle Count", integer(0))
                        ).executes { context ->
                            val count = getInteger(context,"Particle Count")
                            SummonPoisonCloud.PARTICLES_PER_TICK = count
                            count
                        }
                )
                .then(
                    literal("poisonCloudViewDistance")
                        .then(
                            argument("View Distance", doubleArg(0.0))
                        ).executes { context ->
                            val distance = getDouble(context,"View Distance")
                            SummonPoisonCloud.CLOUD_VIEW_DISTANCE = distance
                            distance.toInt()
                        }
                )
                .then(
                    literal("poisonEffectDuration")
                        .then(
                            argument("Duration in ticks", integer(0))
                        ).executes { context ->
                            val duration = getInteger(context,"Duration in ticks")
                            SummonPoisonCloud.POISON_DURATION = duration
                            duration
                        }
                )
                .then(
                    literal("poisonEffectAmplifier")
                        .then(
                            argument("Amplifier", integer(0))
                        ).executes { context ->
                            val amplifier = getInteger(context,"Amplifier")
                            SummonPoisonCloud.POISON_AMPLIFIER = amplifier
                            amplifier
                        }
                )
                .then(
                    literal("tntExplodeTicks")
                        .then(
                            argument("Ticks", integer(0))
                        ).executes { context ->
                            val ticks = getInteger(context,"Ticks")
                            SpawnTntRandomPlayer.TICKS_TO_EXPLODE = ticks
                            ticks
                        }
                )
                .then(
                    literal("teleportHeight")
                        .then(
                            argument("Height", doubleArg(0.0))
                        ).executes { context ->
                            val height = getDouble(context,"Height")
                            TeleportRandomPlayer.HEIGHT_ABOVE_GROUND = height
                            height.toInt()
                        }
                )
                .then(
                    literal("cooldown")
                        .then(
                            argument("Ability", testAbility())
                        )
                        .then(
                            argument("Cooldown", integer())
                        ).executes { context ->
                            val ability = getAbility(context,"Ability")
                            val cooldown = getInteger(context,"Cooldown")
                            shadow.cooldownManager.getCooldown(ability.clazz).cooldown = cooldown
                            cooldown
                        }
                )
                .then(
                    literal("initialCooldown")
                        .then(
                            argument("Ability", testAbility())
                        )
                        .then(
                            argument("Cooldown", integer())
                        ).executes { context ->
                            val ability = getAbility(context,"Ability")
                            val cooldown = getInteger(context,"Cooldown")
                            shadow.cooldownManager.getCooldown(ability.clazz).initialCooldown = cooldown
                            cooldown
                        }
                )
        )
    }
}