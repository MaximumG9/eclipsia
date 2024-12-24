package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class HandleEyePickup(private val shadow: Shadow) : Listener {
    @EventHandler
    fun playerItemPickup(e : EntityPickupItemEvent) {
        shadow.eyes[e.item.uniqueId]?.let { displayPair ->
            displayPair.toList().forEach {
                e.entity.world.getEntity(it)?.remove()
            }
        }
    }
}