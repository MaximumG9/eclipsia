package dev.osmii.shadow.game.start

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.util.ItemUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class P2GiveItems(private val shadow: Shadow) {
    fun giveItems() {
        shadow.gameState.currentRoles.forEach { (uuid, role) ->
            val player: Player? = shadow.server.getPlayer(uuid)
            if (player == null) {
                shadow.logger.warning("Player $uuid is null!")
                return@forEach
            }
            player.inventory.clear()
            player.inventory.setItem(0, ItemStack(Material.BREAD, 16))

            if (role == PlayableRole.SHERIFF) {
                val bow = ItemStack(Material.BOW, 1)
                bow.itemMeta = (bow.itemMeta!! as Damageable).apply {
                    this.displayName(MiniMessage.miniMessage().deserialize("<!i><gold>Sheriff's Bow</gold></!i>"))
                    this.isUnbreakable = true
                    this.addEnchant(Enchantment.ARROW_DAMAGE, 1, true)
                    this.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    this.persistentDataContainer.set(
                        Namespace.FORBIDDEN,
                        PersistentDataType.BYTE_ARRAY,
                        ItemUtil.forbidden(drop = true, use = false, move = false, moveContainer = true)
                    )
                    this.persistentDataContainer.set(
                        Namespace.CUSTOM_ID,
                        PersistentDataType.STRING,
                        CID.INVENTORY_SHERIFF_BOW
                    )
                }
                player.inventory.setItem(9, bow)
            }

            val abilitySelector = ItemStack(Material.NETHER_STAR, 1)
            abilitySelector.itemMeta = abilitySelector.itemMeta.apply {
                if (this == null) return@apply

                this.displayName(MiniMessage.miniMessage().deserialize("<!i><gold>Ability Selector</gold></!i>"))

                this.persistentDataContainer.set(
                    Namespace.FORBIDDEN,
                    PersistentDataType.BYTE_ARRAY,
                    ItemUtil.forbidden(drop = true, use = false, move = true, moveContainer = true)
                )
                this.persistentDataContainer.set(
                    Namespace.CUSTOM_ID,
                    PersistentDataType.STRING,
                    CID.HOTBAR_ABILITY_SELECT
                )
                this.persistentDataContainer.set(
                    Namespace.INVISIBLE,
                    PersistentDataType.BOOLEAN,
                    true
                )
            }
            player.inventory.setItem(8, abilitySelector)
        }

        shadow.server.onlinePlayers.forEach { p ->
            p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING,-1,1,false,false))
        }

        P3SpawnEnderEyes(shadow).spawnEnderEyes()
    }
}