package dev.osmii.shadow.commands

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.config.AbilityTestConfig
import dev.osmii.shadow.enums.ShadowTestAbilities
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandConfig(val shadow: Shadow) : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if(args.size <= 2) {
            return false
        }

        if(args[0] == "shadowAbility") {
            AbilityTestConfig.setAbility(ShadowTestAbilities.valueOf(args[1]))
            return true
        }
        return false
    }
}