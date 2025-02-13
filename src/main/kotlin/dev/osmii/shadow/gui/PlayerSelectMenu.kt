package dev.osmii.shadow.gui

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

object PlayerSelectMenu {
    fun buildMenu(
        player: Player,
        cancelButtonText: List<String>,
        page: Int,
        filter: (Player) -> Boolean = { true },
        callback: (user : Player,target : Player) -> Unit,
        shadow: Shadow
    ) {
        val menu = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize("<gray>Select Player</gray>"))

        val players: List<Player> = Bukkit.getOnlinePlayers().filter(filter).chunked(21)[page]
        val items: List<ItemStack> = players.map { p ->
            val item = ItemStack(Material.PLAYER_HEAD)
            item.itemMeta = (item.itemMeta as SkullMeta).apply {
                this.owningPlayer = p
                this.displayName(MiniMessage.miniMessage().deserialize("<gold>${p.name}</gold>"))
                this.persistentDataContainer.set(
                    Namespace.CUSTOM_ID,
                    PersistentDataType.STRING,
                    CID.ABILITY_SELECT_PLAYER,
                )
                this.persistentDataContainer.set(
                    Namespace.ABILITY_SELECT_PLAYER,
                    PersistentDataType.STRING,
                    p.uniqueId.toString(),
                )
            }
            item
        }

        for (i in 0..53) {
            val useless = ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            useless.itemMeta = useless.itemMeta.apply {
                this?.displayName(MiniMessage.miniMessage().deserialize("<gray> </gray>"))
            }
            menu.setItem(i, useless)
        }

        items.forEachIndexed { index, item ->
            menu.setItem((index / 7) * 9 + index % 7 + 10, item)
        }

        if (page > 0) {
            val prev = ItemStack(Material.ARROW)
            prev.itemMeta = prev.itemMeta.apply {
                this?.displayName(MiniMessage.miniMessage().deserialize("<gray>Previous Page</gray>"))
                this?.persistentDataContainer?.set(
                    Namespace.CUSTOM_ID,
                    PersistentDataType.STRING,
                    CID.ABILITY_SELECT_PLAYER_PREV,
                )
                this?.persistentDataContainer?.set(
                    Namespace.ABILITY_SELECT_PLAYER_PAGE,
                    PersistentDataType.INTEGER,
                    page - 1,
                )
            }
            menu.setItem(48, prev)
        }
        if (page < Bukkit.getOnlinePlayers().filter(filter).chunked(21).size - 1) {
            val next = ItemStack(Material.ARROW)
            next.itemMeta = next.itemMeta.apply {
                this?.displayName(MiniMessage.miniMessage().deserialize("<gray>Next Page</gray>"))
                this?.persistentDataContainer?.set(
                    Namespace.CUSTOM_ID,
                    PersistentDataType.STRING,
                    CID.ABILITY_SELECT_PLAYER_NEXT,
                )
                this?.persistentDataContainer?.set(
                    Namespace.ABILITY_SELECT_PLAYER_PAGE,
                    PersistentDataType.INTEGER,
                    page + 1,
                )
            }
            menu.setItem(50, next)
        }

        val next = ItemStack(Material.BARRIER)
        next.itemMeta = next.itemMeta.apply {
            this?.displayName(MiniMessage.miniMessage().deserialize("<red>Cancel</red>"))
            this.lore(cancelButtonText.map {
                MiniMessage.miniMessage().deserialize(it)
            })
            this?.persistentDataContainer?.set(
                Namespace.CUSTOM_ID,
                PersistentDataType.STRING,
                CID.ABILITY_SELECT_PLAYER_CANCEL,
            )
        }

        shadow.guiCallbacks[menu] = { p, _, item ->
            when(item.itemMeta.persistentDataContainer[Namespace.CUSTOM_ID,PersistentDataType.STRING]) {
                CID.ABILITY_SELECT_PLAYER -> {
                    p.closeInventory()
                    item.itemMeta.persistentDataContainer[Namespace.ABILITY_SELECT_PLAYER,PersistentDataType.STRING]?.let {
                        shadow.server.getPlayer(UUID.fromString(it))?.let { it1 -> callback.invoke(p, it1) }
                    }
                }
                CID.ABILITY_SELECT_PLAYER_CANCEL -> {
                    p.closeInventory()
                }
                CID.ABILITY_SELECT_PLAYER_PREV -> {
                    p.closeInventory()
                    item.itemMeta.persistentDataContainer[Namespace.ABILITY_SELECT_PLAYER_PAGE,PersistentDataType.INTEGER]?.let {
                        this.buildMenu(
                            player,
                            cancelButtonText,
                            it,
                            filter,
                            callback,
                            shadow
                        )
                    }
                }
                CID.ABILITY_SELECT_PLAYER_NEXT -> {
                    p.closeInventory()
                    item.itemMeta.persistentDataContainer[Namespace.ABILITY_SELECT_PLAYER_PAGE,PersistentDataType.INTEGER]?.let {
                        this.buildMenu(
                        player,
                            cancelButtonText,
                            it,
                            filter,
                            callback,
                            shadow
                        )
                    }
                }
            }
        }

        player.openInventory(menu)

        return
    }
}