package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.game.end.GameEnd
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent

class HandleDeath(private val shadow: Shadow) : Listener {
    var checking = false

    // Handles player deaths and sheriff misfires
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return

        // Hide death message
        e.deathMessage(null)

        // Choose color and role
        val p = e.entity
        val color = shadow.gameState.currentRoles[p.uniqueId]?.roleColor
        val message = shadow.gameState.currentRoles[p.uniqueId]?.roleName
        for (player in shadow.server.onlinePlayers) {
            player.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f)
            player.sendMessage(
                Component.text(p.name + " died. They were a ")
                    .color(color)
                    .append(Component.text(message.toString()).color(color))
                    .append(Component.text("."))
            )
            if (shadow.gameState.currentRoles[p.uniqueId]?.roleFaction == PlayableFaction.SHADOW) {
                player.sendMessage(
                    Component.text("There are ")
                        .color(NamedTextColor.RED)
                        .append(
                            Component.text("${shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.SHADOW }.size - 1} ")
                                .color(NamedTextColor.GOLD)
                        )
                        .append(Component.text("shadows remaining.").color(NamedTextColor.RED))
                )
            }
        }

        shadow.gameState.currentRoles[p.uniqueId] = PlayableRole.SPECTATOR

        if (!checking) {
            Bukkit.getScheduler().runTaskLater(shadow, Runnable {
                checking = false
                GameEnd(shadow).checkGameEnd()
                GameEnd(shadow).checkAntiStall()
            }, 20)
            checking = true
        }

        shadow.logger.info(shadow.gameState.currentRoles.toString())
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            val p = e.player
            if (shadow.gameState.currentRoles[p.uniqueId] == PlayableRole.SPECTATOR) {
                e.respawnLocation = shadow.server.worlds[0].spawnLocation
                p.gameMode = GameMode.SPECTATOR
                p.gameMode
            }
        }, 1)


    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onSheriffKill(e: PlayerDeathEvent) {
        if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return

        val p = e.entity.killer
        if (p == null || shadow.gameState.currentRoles[p.uniqueId] != PlayableRole.SHERIFF) return
        if (shadow.gameState.currentRoles[e.entity.uniqueId]?.roleFaction != PlayableFaction.VILLAGE) return

        shadow.server.broadcast(
            MiniMessage.miniMessage()
                .deserialize("<gold>A Sheriff, ${p.name}, has killed an innocent villager. They will be executed for their crimes.</gold>")
        )

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            p.world.strikeLightningEffect(p.location)
            p.damage(99999.9)
        }, 20)
    }
}
