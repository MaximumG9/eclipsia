package dev.osmii.shadow.events.gui

import dev.osmii.shadow.Shadow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class HandleGUICallback(val shadow: Shadow) : Listener {
    @EventHandler
    fun onGUIClick(e : InventoryClickEvent) {
        val item = e.currentItem ?: return
        val clicker = e.whoClicked
        if(clicker is Player) {
            val callback = shadow.guiCallbacks[e.inventory]
            if(callback != null) {
                callback.invoke(clicker, e.inventory, item)
                e.currentItem = null
            }
        }
    }

    @EventHandler
    fun onGUIClose(e : InventoryCloseEvent) {
        shadow.guiCallbacks.remove(e.inventory)
    }
}