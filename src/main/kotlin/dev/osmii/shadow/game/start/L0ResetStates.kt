package dev.osmii.shadow.game.start

import dev.osmii.shadow.Shadow
import org.bukkit.GameMode
import org.bukkit.advancement.AdvancementProgress

class L0ResetStates(private var shadow: Shadow) {
    fun resetStates() {
        shadow.server.getWorld("world")?.time = 0
        shadow.server.getWorld("world")?.fullTime = 0
        shadow.server.getWorld("world")?.setStorm(false)
        shadow.server.getWorld("world")?.isThundering = false
        shadow.server.onlinePlayers.forEach { player ->
            // Reset player data
            player.gameMode = GameMode.ADVENTURE;
            player.health = 20.0
            player.foodLevel = 20
            player.saturation = 20.0f
            player.exp = 0.0f
            player.level = 0
            player.totalExperience = 0

            // Clear inventory and potion effects
            player.inventory.clear()
            player.enderChest.clear()
            player.activePotionEffects.forEach { effect ->
                player.removePotionEffect(effect.type)
            }

            // Clear advancements
            val it = shadow.server.advancementIterator()
            while (it.hasNext()) {
                val adv = it.next()
                val progress: AdvancementProgress = player.getAdvancementProgress(adv)
                for (criteria in progress.awardedCriteria) {
                    progress.revokeCriteria(criteria)
                }
            }
        }
    }
}