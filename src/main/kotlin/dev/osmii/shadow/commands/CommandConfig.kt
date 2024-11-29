package dev.osmii.shadow.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg
import com.mojang.brigadier.arguments.DoubleArgumentType.getDouble
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.config.AbilityTestConfig
import dev.osmii.shadow.enums.ShadowTestAbilities
import dev.osmii.shadow.game.abilities.shadow.ScalingDamageAll
import dev.osmii.shadow.game.abilities.shadow.SpawnTntRandomPlayer
import dev.osmii.shadow.game.abilities.shadow.SummonPoisonCloud
import dev.osmii.shadow.game.abilities.shadow.TeleportRandomPlayer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

class CommandConfig(val shadow: Shadow) {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("\$config")
                .then(
                    literal("shadowAbility")
                        .then(
                            argument("Ability", string())
                                .suggests(this::suggestAbility)
                                .executes { context ->
                                    val ability = getAbility(context,"Ability")
                                    AbilityTestConfig.setAbility(ability)
                                    context.source.sendSuccess({Component.literal("Set second shadow ability to $ability")}, true)
                                    return@executes 0
                                }
                        )
                )
                .then(
                    literal("poisonCloudRadius")
                        .then(
                            argument("Radius",doubleArg(0.0))
                                .executes { context ->
                                    val radius = getDouble(context,"Radius")
                                    SummonPoisonCloud.RADIUS = radius
                                    context.source.sendSuccess({Component.literal("Set poison cloud radius to $radius")}, true)
                                    return@executes radius.toInt()
                                }
                        )
                )
                .then(
                    literal("poisonCloudDuration")
                        .then(
                            argument("Duration in ticks", integer(0))
                                .executes { context ->
                                    val duration = getInteger(context,"Duration in ticks")
                                    SummonPoisonCloud.LIFETIME = duration
                                    context.source.sendSuccess({Component.literal("Set poison cloud duration to $duration")}, true)
                                    return@executes duration
                                }
                        )
                )
                .then(
                    literal("poisonCloudParticlesPerTick")
                        .then(
                            argument("Particle Count", integer(0))
                                .executes { context ->
                                    val count = getInteger(context,"Particle Count")
                                    SummonPoisonCloud.PARTICLES_PER_TICK = count
                                    context.source.sendSuccess({Component.literal("Set poison cloud particles per tick to $count")}, true)
                                    return@executes count
                                }
                        )
                )
                .then(
                    literal("poisonCloudViewDistance")
                        .then(
                            argument("View Distance", doubleArg(0.0))
                                .executes { context ->
                                    val distance = getDouble(context,"View Distance")
                                    SummonPoisonCloud.CLOUD_VIEW_DISTANCE = distance
                                    context.source.sendSuccess({Component.literal("Set poison cloud view distance to $distance")}, true)
                                    return@executes distance.toInt()
                                }
                        )
                )
                .then(
                    literal("poisonEffectDuration")
                        .then(
                            argument("Duration in ticks", integer(0))
                                .executes { context ->
                                    val duration = getInteger(context,"Duration in ticks")
                                    SummonPoisonCloud.POISON_DURATION = duration
                                    context.source.sendSuccess({Component.literal("Set poison cloud effect duration to $duration")}, true)
                                    return@executes duration
                                }
                        )
                )
                .then(
                    literal("poisonEffectAmplifier")
                        .then(
                            argument("Amplifier", integer(0))
                                .executes { context ->
                                    val amplifier = getInteger(context,"Amplifier")
                                    SummonPoisonCloud.POISON_AMPLIFIER = amplifier
                                    context.source.sendSuccess({Component.literal("Set poison cloud effect amplifier to $amplifier")}, true)
                                    return@executes amplifier
                                }
                        )
                )
                .then(
                    literal("tntExplodeTicks")
                        .then(
                            argument("Ticks", integer(0))
                                .executes { context ->
                                val ticks = getInteger(context,"Ticks")
                                SpawnTntRandomPlayer.TICKS_TO_EXPLODE = ticks
                                context.source.sendSuccess({Component.literal("Set tnt explode ticks to $ticks")}, true)
                                return@executes ticks
                            }
                        )
                )
                .then(
                    literal("teleportHeight")
                        .then(
                            argument("Height", doubleArg(0.0))
                                .executes { context ->
                                    val height = getDouble(context,"Height")
                                    TeleportRandomPlayer.HEIGHT_ABOVE_GROUND = height
                                    context.source.sendSuccess({Component.literal("Set teleport height to $height blocks")}, true)
                                    return@executes height.toInt()
                                }
                        )
                )
                .then(
                    literal("cooldown")
                        .then(
                            argument("Ability", string())
                                .suggests(this::suggestAbility)
                                .then(
                                    argument("Cooldown", integer())
                                        .executes { context ->
                                            val ability = getAbility(context,"Ability")
                                            val cooldown = getInteger(context,"Cooldown")
                                            shadow.cooldownManager.getCooldown(ability.clazz).cooldown = cooldown
                                            context.source.sendSuccess({Component.literal("Set cooldown for $ability to $cooldown ticks")}, true)
                                            return@executes cooldown
                                        }
                                )
                        )

                )
                .then(
                    literal("initialCooldown")
                        .then(
                            argument("Ability", string())
                                .suggests(this::suggestAbility)
                                .then(
                                    argument("Cooldown", integer())
                                        .executes { context ->
                                            val ability = getAbility(context,"Ability")
                                            val cooldown = getInteger(context,"Cooldown")
                                            shadow.cooldownManager.getCooldown(ability.clazz).initialCooldown = cooldown
                                            context.source.sendSuccess({Component.literal("Set initial cooldown for $ability to $cooldown ticks")}, true)
                                            return@executes cooldown
                                        }
                                )
                        )
                )
                .then(
                    literal("cullNightly")
                        .then(
                            argument("newState", bool())
                                .executes { context ->
                                    val newState = getBool(context,"newState")
                                    ScalingDamageAll.toggleScalingDamageAllNightly = newState
                                    context.source.sendSuccess({Component.literal("Set cullNightly to ${newState}")}, true)
                                    return@executes if (newState) 1 else 0
                                }
                        )
                        .executes { context ->
                            context.source.sendSuccess({Component.literal("CullNightly is ${ScalingDamageAll.toggleScalingDamageAllNightly}")}, true)
                            return@executes if (ScalingDamageAll.toggleScalingDamageAllNightly) 1 else 0
                        }
                )
        )
    }

    private fun getAbility(ctx: CommandContext<CommandSourceStack>, name: String) : ShadowTestAbilities {
        try {
            return ShadowTestAbilities.valueOf(ctx.getArgument(name, String::class.java))
        } catch (e : IllegalArgumentException) {
            throw INVALID_ABILITY_EXCEPTION_TYPE.create(name)
        }
    }

    private fun suggestAbility(ctx : CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) : CompletableFuture<Suggestions> {
        ShadowTestAbilities.entries.filter {
            it.name.startsWith(builder.remaining,false)
        }.forEach {
            builder.suggest(it.name)
        }
        return builder.buildFuture()
    }

    companion object {
        private val INVALID_ABILITY_EXCEPTION_TYPE =
            DynamicCommandExceptionType {
                value -> LiteralMessage("Ability $value does not exist.")
            }
    }
}