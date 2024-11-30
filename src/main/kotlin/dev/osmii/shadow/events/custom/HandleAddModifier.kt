package dev.osmii.shadow.events.custom

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.RoleModifier
import dev.osmii.shadow.game.rolelist.rolemodifierlist.RoleModifierListGUI
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class HandleAddModifier(val shadow: Shadow) : Listener {
    @EventHandler
    fun onAddRole(e: InventoryClickEvent) {
        if (e.currentItem == null) return
        if (!ItemUtil.customIdIs(e.currentItem!!, CID.MODIFIER_SELECT_ADD_MODIFIER)) return

        val data = e.currentItem!!.itemMeta?.persistentDataContainer?.getOrDefault(
            Namespace.MODIFIER_SELECT_ADD_MODIFIER,
            PersistentDataType.STRING,
            ""
        ) ?: return

        shadow.gameState.roleModifierList.addModifier(RoleModifier.valueOf(data))

        e.isCancelled = true
        e.whoClicked.setItemOnCursor(null)

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            e.whoClicked.closeInventory()
            RoleModifierListGUI(shadow).showBook(e.whoClicked as Player)
        }, 1)
    }
}