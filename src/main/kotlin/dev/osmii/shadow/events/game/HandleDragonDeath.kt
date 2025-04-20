package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class HandleDragonDeath(val shadow : Shadow) : Listener {

    @EventHandler
    fun dragonDeath(e : EntityDeathEvent) {
        if(shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return
        if(e.entityType != EntityType.ENDER_DRAGON) return

        shadow.server.broadcast(MiniMessage.miniMessage().deserialize("<green>The villagers have won!</green>"))

        shadow.server.onlinePlayers.forEach { p ->
            p.showTitle(
                Title.title(
                    MiniMessage.miniMessage().deserialize("<green>Villagers Win</green>"),
                    Component.empty(),
                    Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(70), TimeUtil.ticks(20))
                )
            )
        }

        shadow.gameState.originalRoles.forEach { (uuid, role) ->
            if (role.roleFaction == PlayableFaction.VILLAGE) {
                if (shadow.server.getPlayer(uuid) == null) return@forEach
                shadow.gameState.currentWinners.add(shadow.server.getPlayer(uuid)!!)
            }
        }

        shadow.gameState.startTick = -1
        shadow.gameState.currentPhase = GamePhase.IN_BETWEEN_ROUND

        shadow.server.onlinePlayers.forEach { p ->
            p.clearActivePotionEffects()
        }

        shadow.server.onlinePlayers.forEach { p ->
            p.sendMessage(
                Component.text("Game winners: ")
                    .color(NamedTextColor.BLUE)
                    .append(
                        Component.join(
                            JoinConfiguration.separator(Component.text(", ").color(NamedTextColor.BLUE)),
                            shadow.gameState.currentWinners.map { Component.text(it.name).color(NamedTextColor.GOLD) }
                        )
                    )
            )
        }
    }
}