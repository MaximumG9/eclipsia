package dev.osmii.shadow.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableRole
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.EntityArgument.getPlayer
import net.minecraft.commands.arguments.EntityArgument.player
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

class CommandDebug(val shadow: Shadow) {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("\$debug")
                .then(
                    literal("currentRoles")
                        .executes { source ->
                            val strings: List<String> = shadow.gameState.currentRoles.map { entry ->
                                return@map (shadow.server.getPlayer(entry.key)?.name ?: entry.key.toString()) + ": " + entry.value.name
                            }
                            source.source.sendSuccess({
                                Component.literal(
                                    strings.fold("Roles:\n") { acc, s -> acc + s + "\n" }
                                )},false)
                            return@executes 0
                        }
                )
                .then(
                    literal("setRole")
                        .then(
                            argument("player", player())
                                .then(
                                    argument("role", string())
                                        .suggests(this::suggestRole)
                                        .executes { ctx ->
                                            val role = getRole(ctx, "role")
                                            val player = getPlayer(ctx, "player")

                                            shadow.gameState.currentRoles[player.uuid] = role

                                            shadow.gameState.originalRoles[player.uuid] = role

                                            return@executes 1
                                        }
                                )
                        )

                )
        )
    }

    private fun suggestRole(ctx : CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) : CompletableFuture<Suggestions> {
        PlayableRole.entries.filter {
            it.name.startsWith(builder.remaining,false)
        }.forEach {
            builder.suggest(it.name)
        }
        return builder.buildFuture()
    }

    private fun getRole(ctx: CommandContext<CommandSourceStack>, name: String) : PlayableRole {
        try {
            return PlayableRole.valueOf(ctx.getArgument(name, String::class.java))
        } catch (e : IllegalArgumentException) {
            throw INVALID_ROLE_EXCEPTION_TYPE.create(name)
        }
    }

    companion object {
        private val INVALID_ROLE_EXCEPTION_TYPE =
            DynamicCommandExceptionType {
                    value -> LiteralMessage("Ability $value does not exist.")
            }
    }
}