package dev.osmii.shadow.game.abilities.villagers

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.game.abilities.Ability
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class GetHeart(val shadow: Shadow) : Ability {
    override val id = "swap-players"
    override val item: ItemStack = ItemStack(Material.ENDER_PEARL)

    private val heartItem : ItemStack = ItemStack(Material.ENCHANTED_GOLDEN_APPLE)

    init {
        heartItem.itemMeta.apply {
            this.displayName(Component.text("Heart").color(NamedTextColor.RED))
            this.persistentDataContainer.set(
                Namespace.CUSTOM_ID,
                PersistentDataType.STRING,
                CID.HEART
            )
        }
    }

    override fun apply(player: Player, shadow: Shadow): Component? {
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        if(maxHealth!!.value > 2) {
            maxHealth.addModifier(AttributeModifier("removeHeart",-2.0,AttributeModifier.Operation.ADD_NUMBER))
            player.inventory.addItem(heartItem.clone())
        }

        return null
    }
}