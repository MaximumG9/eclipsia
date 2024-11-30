package dev.osmii.shadow.commands

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.game.rolelist.rolemodifierlist.RoleModifierListGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRoleModifiers(val shadow: Shadow) : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val player = commandSender as Player

        if (args.isEmpty()) {
            RoleModifierListGUI(shadow).showBook(player)
            return false
        }

        when (args[0]) {
            "add" -> {
                if (args.size < 2) {
                    RoleModifierListGUI(shadow).showAddRoleModifierInventory(player)
                    return false
                }
            }
            "remove" -> {
                if (args.size < 3) return false

                val page = args[1].toInt()
                val index = args[2].toInt()
                val id = page * 5 + index
                // Ensure that the ID to remove actually exists
                if(index == -1 || id > shadow.gameState.roleModifierList.getModifiers().size || id < 0) return false
                shadow.gameState.roleModifierList.removeModifierAtID(id)
                RoleModifierListGUI(shadow).showBook(player)
            }
            "inc" -> {
                if (args.size < 3) return false

                val page = args[1].toInt()
                val index = args[2].toInt()
                val id = page * 5 + index
                // Ensure that the ID to remove actually exists
                if(index == -1 || id > shadow.gameState.roleModifierList.getModifiers().size || id < 0) return false
                shadow.gameState.roleModifierList.addModifierAtID(id)
                RoleModifierListGUI(shadow).showBook(player)
                return false
            }
            "dec" -> {
                if (args.size < 3) return false

                val page = args[1].toInt()
                val index = args[2].toInt()
                val id = page * 5 + index
                // Ensure that the ID to remove actually exists
                if(index == -1 || id > shadow.gameState.roleModifierList.getModifiers().size || id < 0) return false
                shadow.gameState.roleModifierList.removeModifierAtID(id)
                RoleModifierListGUI(shadow).showBook(player)
                return false
            }
            "incperc" -> {
                if (args.size < 4) return false

                val page = args[1].toInt()
                val index = args[2].toInt()
                val id = page * 5 + index

                val delta = args[3].toInt()
                // Ensure that the ID to remove actually exists
                if(index == -1 || id > shadow.gameState.roleModifierList.getModifiers().size || id < 0) return false
                shadow.gameState.roleModifierList.increaseModifierChanceAtID(id,delta)
                RoleModifierListGUI(shadow).showBook(player)
                return false
            }
            "decperc" -> {
                if (args.size < 4) return false

                val page = args[1].toInt()
                val index = args[2].toInt()
                val id = page * 5 + index

                val delta = args[3].toInt()
                // Ensure that the ID to remove actually exists
                if(index == -1 || id > shadow.gameState.roleModifierList.getModifiers().size || id < 0) return false
                shadow.gameState.roleModifierList.decreaseModifierChanceAtID(id,delta)
                RoleModifierListGUI(shadow).showBook(player)
                return false
            }
        }

        return false
    }
}