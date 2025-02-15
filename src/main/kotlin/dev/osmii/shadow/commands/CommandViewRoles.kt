package dev.osmii.shadow.commands

import com.mojang.brigadier.CommandDispatcher
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.gui.RolelistGUI
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal

class CommandViewRoles(val shadow: Shadow) {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("\$viewRoles")
                .executes { ctx ->
                    val player = ctx.source.player
                    if(player == null) {
                        return@executes -1
                    } else {
                        RolelistGUI(shadow).showRoleBook(player.bukkitEntity)
                        return@executes 1
                    }
                }
        )
    }
}