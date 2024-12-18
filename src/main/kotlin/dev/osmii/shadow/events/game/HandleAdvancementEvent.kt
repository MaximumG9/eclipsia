package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class HandleAdvancementEvent(val shadow: Shadow) : Listener {
    @EventHandler
    fun onPlayerAdvancement(e: PlayerAdvancementDoneEvent) {
        if(shadow.gameState.currentRoles[e.player.uniqueId]?.roleFaction == PlayableFaction.SPECTATOR) return
        e.message(e.message()?.replaceText { configurer ->
            configurer.match(".*").replacement("??")
        })
    }
}