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
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.*

class HandleDeath(private val shadow: Shadow) : Listener {
    private var checking = false

    // Handles player deaths and sheriff misfires
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return

        // Hide death message
        e.deathMessage(null)

        // Choose color and role
        val p = e.entity
        val playerRole = shadow.gameState.currentRoles[p.uniqueId]
        val color = playerRole?.roleColor
        val message = playerRole?.roleName

        e.deathMessage(
            Component.text(p.name + " died. They were a ")
                .color(color)
                .append(Component.text(message.toString()).color(color))
                .append(Component.text("."))
                .apply {
                    if (playerRole?.roleFaction == PlayableFaction.SHADOW) {
                        this.append(Component.text("There are "))
                            .color(NamedTextColor.RED)
                            .append(
                                Component.text("${shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.SHADOW }.size - 1} ")
                                    .color(NamedTextColor.GOLD)
                            )
                            .append(Component.text("shadows remaining.").color(NamedTextColor.RED))
                    }
                }
        )

        shadow.gameState.currentRoles[p.uniqueId] = PlayableRole.SPECTATOR

        val possibleJesterCooldown = shadow.jesterCooldowns[p.uniqueId]

        val justifiedDeadJesters : Set<UUID> = mutableSetOf()

        if(possibleJesterCooldown != null) {
            if(shadow.jesterCooldowns[p.uniqueId]!!.checkCooldown(p) <= 0) {
                justifiedDeadJesters.plus(p.uniqueId)
            }
        }

        if (!checking) {
            Bukkit.getScheduler().runTaskLater(shadow, Runnable {
                checking = false
                GameEnd(shadow).checkGameEnd(justifiedDeadJesters)
                GameEnd(shadow).checkAntiStall()
            }, 20)
            checking = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            val p = e.player
            if (shadow.gameState.currentRoles[p.uniqueId] == PlayableRole.SPECTATOR) {
                e.respawnLocation = shadow.overworld.spawnLocation
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
