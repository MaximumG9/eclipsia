package dev.osmii.shadow.game.start

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

class P1AssignRoles(private val shadow: Shadow) {
    fun assignRoles() {
        var players = ArrayList<Player>(shadow.server.onlinePlayers)
        players = players.filter {
            shadow.gameState.participationStatus.getOrDefault(
                it.uniqueId,
                false
            )
        } as ArrayList<Player>

        if (players.size < shadow.gameState.originalRolelist.roles.size) {
            shadow.server.broadcast(
                MiniMessage.miniMessage().deserialize(
                    "<red>Failed to start game. Not enough participating players!</red> <gold>(${players.size}/${shadow.gameState.originalRolelist.roles.size})</gold>"
                )
            )
            shadow.gameState.currentPhase = GamePhase.IN_BETWEEN_ROUND
            return
        }

        shadow.gameState.currentRoles.clear()

        shadow.gameState.participationStatus.forEach { (player, _) ->
            if (!shadow.gameState.participationStatus[player]!!) {
                shadow.gameState.currentRoles[player] = PlayableRole.SPECTATOR
            }
        }

        // Assign roles
        shadow.gameState.originalRolelist.pickRoles()
        shadow.gameState.originalRolelist.pickedRoles.shuffle()
        players.shuffle()
        for (i in 0..<shadow.gameState.originalRolelist.roles.size) {
            shadow.gameState.currentRoles[players[i].uniqueId] = shadow.gameState.originalRolelist.pickedRoles[i]
        }

        shadow.gameState.originalRoles = shadow.gameState.currentRoles.clone() as HashMap<UUID, PlayableRole>
        shadow.logger.info(shadow.gameState.currentRoles.toString())

        // Send roles to players
        shadow.gameState.currentRoles.forEach { (uuid, role) ->
            val player: Player? = shadow.server.getPlayer(uuid)
            if (player == null) {
                shadow.logger.warning("Player $uuid is null!")
                return@forEach
            }
            if (role.roleFaction == PlayableFaction.SPECTATOR) player.sendMessage(
                MiniMessage.miniMessage().deserialize("<gray><i>You are spectating this game.</i></gray>")
            )
            if (role.roleFaction == PlayableFaction.SHADOW) player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("<red>You are a shadow. Protect the dragon, kill all villagers, and stay hidden.</red>")
            )
            if (role.roleFaction == PlayableFaction.VILLAGE) player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("<green>You are a villager. Kill the dragon, or find the shadows, and stay alive.</green>")
            )

            if (role.roleFaction == PlayableFaction.SHADOW) player.showTitle(
                Title.title(
                    MiniMessage.miniMessage().deserialize("<red>You are a Shadow.</red>"),
                    MiniMessage.miniMessage().deserialize("<red>Protect the dragon. Kill the villagers.</red>"),
                    Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(40), TimeUtil.ticks(10))
                )
            )
            if (role.roleFaction == PlayableFaction.VILLAGE) player.showTitle(
                Title.title(
                    MiniMessage.miniMessage().deserialize("<green>You are a Villager.</green>"),
                    MiniMessage.miniMessage().deserialize("<green>Kill the dragon and stay alive.</green>"),
                    Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(40), TimeUtil.ticks(10))
                )
            )

            if (role.roleFaction == PlayableFaction.NEUTRAL) player.showTitle(
                Title.title(
                    MiniMessage.miniMessage().deserialize("<gray>You are Neutral.</gray>"),
                    MiniMessage.miniMessage().deserialize("<gray>Achieve your own goals for victory.</gray>"),
                    Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(40), TimeUtil.ticks(10))
                )
            )

            // Set gamemodes
            if (role != PlayableRole.SPECTATOR) player.gameMode = GameMode.SURVIVAL
            if (role == PlayableRole.SPECTATOR) player.gameMode = GameMode.SPECTATOR

            Bukkit.getScheduler().runTaskLater(shadow, Runnable {
                if (role.roleFaction != PlayableFaction.SPECTATOR) player.sendMessage(
                    Component.text("Your role is: ")
                        .color(NamedTextColor.GRAY)
                        .append(Component.text(role.roleName).color(role.roleColor))
                )

                player.showTitle(
                    Title.title(
                        Component.text(role.roleName).color(role.roleColor),
                        Component.text(role.roleDescription).color(role.roleColor),
                        Title.Times.times(TimeUtil.ticks(10), TimeUtil.ticks(40), TimeUtil.ticks(10))
                    )
                )

            }, 100)

            // /shadowchat tip
            if (role.roleFaction == PlayableFaction.SHADOW) {
                player.sendMessage(
                    Component.text("The shadows are: ")
                        .color(NamedTextColor.RED)
                        .append(
                            Component.join(
                                JoinConfiguration.separator(Component.text(", ").color(NamedTextColor.RED)),
                                shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.SHADOW }.keys.map {
                                    Component.text(shadow.server.getPlayer(it)?.name!!).color(NamedTextColor.GOLD)
                                }
                            )
                        )
                )

                player.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize("<red><i>You can use <gold>/sc <message></gold> to talk to other shadows!</i></red>")
                )
            }
        }

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            P2GiveItems(shadow).giveItems()
        }, 110)
    }
}