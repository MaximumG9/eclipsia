package dev.osmii.shadow.commands

import com.mojang.brigadier.CommandDispatcher
import dev.osmii.shadow.Shadow
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component

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
        )
    }
}