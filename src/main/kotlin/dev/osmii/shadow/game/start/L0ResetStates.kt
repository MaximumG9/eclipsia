package dev.osmii.shadow.game.start

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.game.end.GameEnd
import org.bukkit.GameMode
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.attribute.Attribute

class L0ResetStates(private val shadow: Shadow) {
    fun resetStates() {
        val overworld = shadow.overworld

        overworld.time = 0
        overworld.fullTime = 0
        overworld.setStorm(false)
        overworld.isThundering = false

        GameEnd.timerStarted = false

        shadow.abilityManager.clear()

        shadow.gameState.currentRoleModifiers.clear()

        shadow.jesterCooldowns.clear()

        shadow.server.onlinePlayers.forEach { player ->
            // Reset player data
            player.gameMode = GameMode.ADVENTURE
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
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