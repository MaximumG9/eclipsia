package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.ItemUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ToggleStrength : Ability {
    override val item: ItemStack = ItemStack(Material.POTION)

    override val id = "ASSASSINATE"

    private var strength = false

    fun isStrength() : Boolean {
        return strength
    }

    init {
        item.itemMeta = (item.itemMeta as PotionMeta).apply {
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Empower</red></!i>"))
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Gain</gray> <blue>Strength I</blue> <gray>.</gray></!i>")
                )
            )
            this.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            this.addCustomEffect(
                PotionEffect(
                    PotionEffectType.INCREASE_DAMAGE,
                    -1,
                    0,
                    false,
                    false,
                    true
                ), true
            )
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


    override fun apply(player: Player, shadow: Shadow) {
        strength = !strength

        player.sendMessage(
            MiniMessage.miniMessage()
                .deserialize("<red>Toggled strength</red> <blue>${if (strength) "on" else "off"}</blue><red>.</red>")
        )
        if (strength) player.addPotionEffect(
            PotionEffect(
                PotionEffectType.INCREASE_DAMAGE, -1, 0,
                false, false, true
            )
        )
        else player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)

    }
}