package dev.osmii.shadow.events

import dev.osmii.shadow.enums.Namespace
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class HandleItemInteractionRestrict : Listener {
    // Blocks item dropping, moving, and using for items with the forbidden tag
    @EventHandler
    fun onForbiddenItemDrop(e: PlayerDropItemEvent) {
        if (e.itemDrop.itemStack.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden = e.itemDrop.itemStack.itemMeta?.persistentDataContainer?.get(
            Namespace.FORBIDDEN,
            PersistentDataType.BYTE_ARRAY
        )

        if (forbidden?.get(0) == 1.toByte()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onForbiddenItemDrop(e: PlayerDeathEvent) {
        val modified = ArrayList<ItemStack>()
        for (item in e.drops) {
            if (item.itemMeta?.persistentDataContainer?.has(
                    Namespace.FORBIDDEN,
                    PersistentDataType.BYTE_ARRAY
                ) == false
            ) {
                modified.add(item)
                continue
            }

            val forbidden =
                item.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

            if (forbidden?.get(0) != 1.toByte()) {
                modified.add(item)
            }
        }

        e.drops.clear()
        e.drops.addAll(modified)
    }

    @EventHandler
    fun onForbiddenItemInteraction(e: PlayerInteractEvent) {
        if (e.item?.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden =
            e.item?.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        if (forbidden?.get(1) == 1.toByte()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onForbiddenItemMove(e: InventoryMoveItemEvent) {
        if (e.item.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden =
            e.item.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        if (forbidden?.get(2) == 1.toByte()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onForbiddenItemMove(e: InventoryClickEvent) {
        if (e.currentItem == null) return
        if (e.currentItem?.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden = e.currentItem?.itemMeta?.persistentDataContainer?.get(
            Namespace.FORBIDDEN,
            PersistentDataType.BYTE_ARRAY
        )

        if (forbidden?.get(2) == 1.toByte()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onForbiddenItemMoveCursor(e: InventoryClickEvent) {
        if (e.cursor.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden =
            e.cursor.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        if (forbidden?.get(2) == 1.toByte()) {
            e.isCancelled = true
            e.whoClicked.setItemOnCursor(ItemStack(Material.AIR))
        }
    }

    @EventHandler
    fun onForbiddenItemMove(e: InventoryCreativeEvent) {
        if (e.currentItem == null) return
        if (e.currentItem?.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden =
            e.cursor.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        if (forbidden?.get(2) == 1.toByte()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onForbiddenItemMove(e: InventoryDragEvent) {
        for (item in e.newItems.values) {
            if (item.itemMeta?.persistentDataContainer?.has(
                    Namespace.FORBIDDEN,
                    PersistentDataType.BYTE_ARRAY
                ) == true
            ) continue

            val forbidden =
                item.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

            if (forbidden?.get(2) == 1.toByte()) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onForbiddenItemMoveContainer(e: InventoryMoveItemEvent) {
        if (e.source == e.destination) return

        if (e.item.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == false
        ) return

        val forbidden =
            e.item.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        if (forbidden?.get(3) == 1.toByte()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onForbiddenItemOffhand(e: PlayerSwapHandItemsEvent) {
        if (!(e.mainHandItem.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == true || (e.offHandItem.itemMeta?.persistentDataContainer?.has(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY
            ) == true))
        ) return

        val forbiddenMainHand =
            e.mainHandItem.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        val forbiddenOffHand =
            e.offHandItem.itemMeta?.persistentDataContainer?.get(Namespace.FORBIDDEN, PersistentDataType.BYTE_ARRAY)

        if (forbiddenMainHand?.get(3) == 1.toByte() || forbiddenOffHand?.get(3) == 1.toByte()) {
            e.isCancelled = true
        }
    }
}