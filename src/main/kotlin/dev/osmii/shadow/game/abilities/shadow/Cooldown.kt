package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.collections.HashMap
import kotlin.math.max


class Cooldown(val shadow: Shadow) {

    private var lastActivationMap : HashMap<UUID,Int> = HashMap()

    var initialCooldown = 3 * 60

    var cooldown = 7 * 60

    // Returns 0 if the cooldown has passed else returns the time until the cooldown expires
    fun checkCooldown(player: Player) : Int {
        if(lastActivationMap[player.uniqueId] == -1) {
            return max((shadow.gameState.startTick + initialCooldown) - shadow.server.currentTick,0)
        } else {
            return max((shadow.gameState.startTick + cooldown) - shadow.server.currentTick,0)
        }

    }

    fun resetCooldown(player: Player) {
        lastActivationMap[player.uniqueId] = shadow.server.currentTick
    }
}