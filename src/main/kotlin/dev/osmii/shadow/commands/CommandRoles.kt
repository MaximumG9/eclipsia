package dev.osmii.shadow.commands

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.gui.RolelistGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRoles(val shadow: Shadow) : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val player = commandSender as Player

        if (args.isEmpty()) {
            RolelistGUI(shadow).showAddRoleBook(player)
            return false
        }

        when (args[0]) {
            "add" -> {
                if (args.size < 2) {
                    RolelistGUI(shadow).showAddRoleInventory(player)
                    return false
                }
            }
            "removeall" -> {
                shadow.gameState.originalRolelist.roles.clear()
                RolelistGUI(shadow).showAddRoleBook(player)
                return false
            }
            "remove" -> {
                if (args.size < 3) return false

                val page = args[1].toInt()
                val index = args[2].toInt()
                val id = page * 14 + index
                // Ensure that the ID to remove actually exists
                if(index == -1 || id > shadow.gameState.originalRolelist.roles.size || id < 0) return false
                shadow.gameState.originalRolelist.roles.removeAt(id)
                RolelistGUI(shadow).showAddRoleBook(player)
                return false
            }
        }

        return false
    }


}
