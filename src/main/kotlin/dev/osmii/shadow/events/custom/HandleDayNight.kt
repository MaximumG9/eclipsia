package dev.osmii.shadow.events.custom

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.game.abilities.shadow.ToggleStrength
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

const val nightSpeedMult = 3;

class HandleDayNight(val shadow: Shadow) {

    val glowingUpdatedFor: ArrayList<Pair<Int, Int>> = ArrayList()

    fun register() {
        val world: World? = shadow.overworld
        Bukkit.getScheduler().runTaskTimer(shadow, Runnable {
            if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return@Runnable

            if (world?.time in 12452L..12452L + nightSpeedMult) {
                glowingUpdatedFor.clear()
                shadow.server.onlinePlayers.forEach { p ->
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.SHADOW) {
                        Audience.audience(p).sendMessage(
                            MiniMessage.miniMessage()
                                .deserialize("<green>Darkness approaches. Your powers grow.</green>")
                        )
                    }
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.VILLAGE ||
                        shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.NEUTRAL
                    ) {
                        Audience.audience(p).sendActionBar(
                            MiniMessage.miniMessage().deserialize("<red>Darkness approaches. It is now nighttime</red>")
                        )
                    }
                }
            }
            if (world?.time in 0L..80L) {
                shadow.server.onlinePlayers.forEach { p ->
                    Audience.audience(p).sendActionBar(
                        MiniMessage.miniMessage().deserialize("<green>The sky clears.</green>")
                    )
                }
            }
            if (world?.time!! >= 12452L) {
                world.time += nightSpeedMult - 1
                shadow.server.onlinePlayers.forEach { p ->
                    if(p.isGlowing) p.isGlowing = false
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.VILLAGE) {
                        p.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, 40, 0, false, false))
                    }
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.SHADOW) {
                        if(ToggleStrength.strength[p] == true) {
                            p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 40, 1, false, false))
                            p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 40, 4, false, false))
                        }
                    }
                }
            } else {
                shadow.server.onlinePlayers.forEach { p ->
                    if(!p.isGlowing) p.isGlowing = true
                }
            }
        }, 0, 1L)
    }
}