package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.game.rolelist.RolelistSelector
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class HandleJoinLeave(private val shadow: Shadow) : Listener {
    // Handle players joining and leaving the server during the game
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        Bukkit.getScoreboardManager().mainScoreboard.getTeam("players")?.addEntry(e.player.name)

        if (shadow.gameState.currentPhase == GamePhase.INITIAL_COUNTDOWN) {
            e.player.kick(
                MiniMessage.miniMessage().deserialize(
                    "<red>The game is starting soon. Please wait for the next round to join.</red>"
                )
            )
            return
        }

        val participating = shadow.gameState.participationStatus.filter {
            it.value
        }.size
        if ((shadow.gameState.currentPhase == GamePhase.LOCATION_SELECTED || shadow.gameState.currentPhase == GamePhase.IN_BETWEEN_ROUND) && participating >= shadow.gameState.originalRolelist.roles.size) {
            shadow.gameState.originalRolelist.addRole(
                RolelistSelector(
                    PlayableRole.VILLAGER
                )
            )
        }

        if(!shadow.isRole(e.player,PlayableRole.LOOKER)) {
            shadow.eyes.values.forEach { uuids ->
                e.player.hideEntity(shadow, shadow.server.getEntity(uuids.second)!!)
            }
        }

        if (shadow.gameState.currentPhase == GamePhase.GAME_IN_PROGRESS && !shadow.gameState.currentRoles.containsKey(e.player.uniqueId)) {
            val p = e.player
            shadow.gameState.currentRoles[p.uniqueId] = PlayableRole.SPECTATOR
            p.gameMode = GameMode.SPECTATOR
        }
    }

}
