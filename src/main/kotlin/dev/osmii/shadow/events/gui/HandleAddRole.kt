package dev.osmii.shadow.events.gui

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.*
import dev.osmii.shadow.game.rolelist.RolelistSelector
import dev.osmii.shadow.gui.RolelistGUI
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class HandleAddRole(val shadow: Shadow) : Listener {
    @EventHandler
    fun onAddRole(e: InventoryClickEvent) {
        if (e.currentItem == null) return
        if (!ItemUtil.customIdIs(e.currentItem!!, CID.ROLE_SELECT_ADD_ROLE)) return

        val data = e.currentItem!!.itemMeta?.persistentDataContainer?.getOrDefault(
            Namespace.ROLE_SELECT_ADD_ROLE,
            PersistentDataType.STRING,
            ""
        ) ?: return
        when (data) {
            "special" -> shadow.gameState.originalRolelist.addRole(RolelistSelector("special"))
            "basic" -> shadow.gameState.originalRolelist.addRole(RolelistSelector("basic"))
            "all" -> shadow.gameState.originalRolelist.addRole(RolelistSelector("all"))
        }
        if (data == "special" || data == "basic" || data == "all") {
            e.isCancelled = true
            e.whoClicked.setItemOnCursor(null)

            Bukkit.getScheduler().runTaskLater(shadow, Runnable {
                e.whoClicked.closeInventory()
                RolelistGUI(shadow).showAddRoleBook(e.whoClicked as Player)
            }, 1)
            return
        }

        val role = data.split("-")[1]
        val type = data.split("-")[0]

        when (type) {
            "role" -> shadow.gameState.originalRolelist.addRole(RolelistSelector(PlayableRole.valueOf(role)))
            "subfaction" -> shadow.gameState.originalRolelist.addRole(RolelistSelector(PlayableSubfaction.valueOf(role)))
            "faction" -> shadow.gameState.originalRolelist.addRole(RolelistSelector(PlayableFaction.valueOf(role)))
        }

        e.isCancelled = true
        e.whoClicked.setItemOnCursor(null)

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            e.whoClicked.closeInventory()
            RolelistGUI(shadow).showAddRoleBook(e.whoClicked as Player)
        }, 1)
    }
}