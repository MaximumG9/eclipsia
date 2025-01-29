package dev.osmii.shadow.game.end

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.GameResult
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.sqrt

class GameEnd(val shadow: Shadow) {
    private var timerTask = AtomicReference<BukkitTask>()
    private var antiStallTask = AtomicReference<BukkitTask>()
    private var damagerTask = AtomicReference<BukkitTask>()

    fun checkGameEnd(justifiedDeadJesters : Optional<UUID>) {
        // Check that the game is actually over (overrides if ender dragon was killed)
        var villagersAlive = false
        var shadowsAlive = false
        for(entry in shadow.gameState.currentRoles) {
            if (entry.value.roleFaction == PlayableFaction.SHADOW) shadowsAlive = true
            if (entry.value.roleFaction == PlayableFaction.VILLAGE) villagersAlive = true
        }
        if (villagersAlive && shadowsAlive && justifiedDeadJesters.isEmpty) return
        if (GamePhase.GAME_IN_PROGRESS != shadow.gameState.currentPhase) return

        // Game is over, determine winner

        val result = when {
            justifiedDeadJesters.isPresent -> GameResult.JESTER_WINS
            !villagersAlive && !shadowsAlive -> GameResult.DRAW
            !villagersAlive -> GameResult.SHADOW_WINS
            !shadowsAlive -> GameResult.VILLAGE_WINS
            else -> GameResult.DRAW
        }

        shadow.gameState.startTick = -1
        shadow.gameState.currentPhase = GamePhase.IN_BETWEEN_ROUND

        when (result) {
            GameResult.SHADOW_WINS -> {
                shadow.server.broadcast(MiniMessage.miniMessage().deserialize("<red>The shadows have won!</red>"))

                shadow.server.onlinePlayers.forEach { p ->
                    p.showTitle(
                        Title.title(
                            MiniMessage.miniMessage().deserialize("<red>Shadows Win</red>"),
                            Component.empty(),
                            Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(70), TimeUtil.ticks(20))
                        )
                    )
                }

                shadow.gameState.originalRoles.forEach { (uuid, role) ->
                    if (role.roleFaction == PlayableFaction.SHADOW) {
                        if (shadow.server.getPlayer(uuid) == null) return@forEach
                        shadow.gameState.currentWinners.add(shadow.server.getPlayer(uuid)!!)
                    }
                }
            }

            GameResult.VILLAGE_WINS -> {
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
            }

            GameResult.DRAW -> {
                shadow.server.broadcast(
                    MiniMessage.miniMessage().deserialize("<gray>The game has ended in a draw!</gray>")
                )
                shadow.server.onlinePlayers.forEach { p ->
                    p.showTitle(
                        Title.title(
                            MiniMessage.miniMessage().deserialize("<gray>Match Draw</gray>"),
                            Component.empty(),
                            Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(70), TimeUtil.ticks(20))
                        )
                    )
                }
            }
            GameResult.JESTER_WINS -> {
                shadow.server.broadcast(MiniMessage.miniMessage().deserialize("<green>The Jester has won!</green>"))

                shadow.server.onlinePlayers.forEach { p ->
                    p.showTitle(
                        Title.title(
                            Component.text("Jester Win").color(PlayableRole.JESTER.roleColor),
                            Component.empty(),
                            Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(70), TimeUtil.ticks(20))
                        )
                    )
                }

                shadow.gameState.originalRoles.forEach { (uuid, role) ->
                    if (role == PlayableRole.JESTER && justifiedDeadJesters.get() == uuid) {
                        if (shadow.server.getPlayer(uuid) == null) return@forEach
                        shadow.gameState.currentWinners.add(shadow.server.getPlayer(uuid)!!)
                    }
                }
            }
        }

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

    fun checkAntiStall() {
        // Check that one villager and one shadow are alive
        val villagersAlive =
            shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.VILLAGE }.size
        val shadowsAlive =
            shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.SHADOW }.size

        if (villagersAlive == shadowsAlive && timerTask.get() == null) {
            // Send anti-stall notification
            shadow.server.onlinePlayers.forEach { p ->
                p.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize("<red>There are as many villagers as shadows left. In ten minutes, anyone not in the end will begin taking damage.</red>")
                )
            }

            val minutes = AtomicInteger(10)
            val seconds = AtomicInteger(0)

            // Send timer action bar message
            timerTask.set(Bukkit.getScheduler().runTaskTimer(shadow, Runnable {
                // If game is no longer in progress, cancel task
                if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) {
                    timerTask.get().cancel()
                    antiStallTask.get().cancel()
                    return@Runnable
                }

                val color: NamedTextColor = when {
                    seconds.get() % 2 == 0 -> NamedTextColor.RED
                    else -> NamedTextColor.GOLD
                }
                shadow.server.onlinePlayers.forEach { p ->
                    Audience.audience(p).sendActionBar(
                        Component.text(minutes.get().toString()).color(color)
                            .append(Component.text(":").color(color))
                            .append(
                                Component.text(
                                    if (seconds.get() < 10) "0${seconds.get()}" else seconds.get().toString()
                                ).color(color)
                            )
                    )
                }

                if (seconds.get() == 0) {
                    minutes.getAndDecrement()
                    seconds.set(59)
                } else {
                    seconds.getAndDecrement()
                }
            }, 0, 20))

            antiStallTask.set(Bukkit.getScheduler().runTaskLater(shadow, Runnable {
                shadow.logger.info("Anti-stall I triggered")
                if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return@Runnable
                timerTask.get()?.cancel()
                antiStallPhase1()
            }, minutes.get() * 60 * 20L + seconds.get() * 20L))
        }
    }

    private fun antiStallPhase1() {
        val alternatingColor = AtomicReference(false)
        val triggers = AtomicReference(0)
        damagerTask.set(Bukkit.getScheduler().runTaskTimer(shadow, Runnable {
            alternatingColor.set(!alternatingColor.get())
            triggers.set(triggers.get() + 1)

            // If game is no longer in progress, cancel task
            if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) {
                damagerTask.get().cancel()
                return@Runnable
            }

            shadow.server.onlinePlayers.forEach { p ->
                val color: NamedTextColor = when {
                    alternatingColor.get() -> NamedTextColor.RED
                    else -> NamedTextColor.GOLD
                }
                if (p.world == shadow.server.worlds[2] || shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.SPECTATOR) return@forEach

                Audience.audience(p).sendActionBar(
                    MiniMessage.miniMessage().deserialize("<$color>You are now taking damage. Get to the end!</$color>")
                )

                if (alternatingColor.get()) {
                    p.damage(sqrt(triggers.get().toDouble()) * 0.5)
                }
            }
        }, 0, 20))
    }
}