package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableRole
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.world.EntitiesLoadEvent

class HandleEyes(private val shadow: Shadow) : Listener {
    @EventHandler
    fun playerItemPickup(e : EntityPickupItemEvent) {
        shadow.eyes[e.item.uniqueId]?.let { displayPair ->
            displayPair.toList().forEach {
                e.entity.world.getEntity(it)?.remove()
            }
        }
    }

    @EventHandler
    fun onLoadEyes(e : EntitiesLoadEvent) {
        val eyeStandUUIDs = shadow.eyes.values.map { eyePair -> eyePair.first }
        e.entities.filter { entity ->
            eyeStandUUIDs.contains(entity.uniqueId)
        }.forEach { eyeStand ->
            shadow.server.onlinePlayers.forEach { player ->
                if(!shadow.isRole(player, PlayableRole.LOOKER)) {
                        player.hideEntity(shadow, eyeStand)
                }
            }
        }
    }
}