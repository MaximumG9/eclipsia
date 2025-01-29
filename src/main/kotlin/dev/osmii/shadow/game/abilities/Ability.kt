package dev.osmii.shadow.game.abilities

import dev.osmii.shadow.Shadow
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Ability {
    val item: ItemStack

    val id: String

    fun apply(player: Player, shadow: Shadow) : Component?
}