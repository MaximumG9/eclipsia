package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.game.abilities.Ability
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class NoopAbility : Ability {
    override val item = ItemStack(Material.LIGHT)

    override val id = "NOOP"
    override fun apply(player: Player, shadow: Shadow) : Component {
        return Component.text("")
    }
}