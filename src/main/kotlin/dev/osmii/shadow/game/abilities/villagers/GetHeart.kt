package dev.osmii.shadow.game.abilities.villagers

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class GetHeart(val shadow: Shadow) : Ability {
    override val id = "get-heart"
    override val item: ItemStack = ItemStack(Material.GLOWSTONE_DUST)

    init {


        item.itemMeta = item.itemMeta.apply {
            this.displayName(Component.text("Get Heart").color(NamedTextColor.RED))
            this.persistentDataContainer.set(
                Namespace.ABILITY_SELECT,
                PersistentDataType.STRING,
                id
            )
            this.persistentDataContainer.set(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY,
                ItemUtil.forbidden(drop = true, use = false, move = false, moveContainer = false)
            )
        }
    }

    override fun apply(player: Player, shadow: Shadow): Component? {
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        if(maxHealth!!.value > 2) {
            maxHealth.addModifier(AttributeModifier("removeHeart",-2.0,AttributeModifier.Operation.ADD_NUMBER))
            player.inventory.addItem(ItemStack(heartItem))
        }

        return null
    }

    companion object {
        val heartItem : ItemStack = ItemStack(Material.ENCHANTED_GOLDEN_APPLE)

        init {
            heartItem.itemMeta = heartItem.itemMeta.apply {
                this.displayName(Component.text("Heart").color(NamedTextColor.RED))
                this.persistentDataContainer.set(
                    Namespace.CUSTOM_ID,
                    PersistentDataType.STRING,
                    CID.HEART
                )
            }
        }
    }
}