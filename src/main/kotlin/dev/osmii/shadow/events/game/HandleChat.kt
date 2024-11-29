package dev.osmii.shadow.events.game

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.math.ceil

class Renderer : ChatRenderer {
    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        return MiniMessage.miniMessage().deserialize(
            "<white>${(source.displayName() as TextComponent).content()} » </white><gray>${(message as TextComponent).content()}</gray>"
        )
    }

}

class HandleChat(private val shadow: Shadow) : Listener {

    private var lastChat: HashMap<Player, Double> = HashMap()

    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        e.renderer(Renderer())

        if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return
        if (e.player.isOp) return

        // If player is spectator, cancel
        if (shadow.isRoleFaction(e.player, PlayableFaction.SPECTATOR)) {
            e.isCancelled = true
            e.player.sendMessage(
                Component.text("You cannot send messages as a spectator.")
                    .color(NamedTextColor.RED)
            )
            return
        }

        // If less than 30 seconds since last chat, cancel
        if (lastChat[e.player] != null && System.currentTimeMillis().toDouble() - lastChat.getOrDefault(
                e.player,
                0.toDouble()
            ) < 30000
        ) {
            val secsLeft = ceil(
                (30000 - (System.currentTimeMillis() - lastChat.getOrDefault(e.player, 0).toDouble())) / 1000
            ).toInt()
            e.isCancelled = true
            e.player.sendMessage(
                Component.text("You must wait ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("$secsLeft seconds").color(NamedTextColor.GOLD))
                    .append(Component.text(" before sending another message."))
            )
            return
        }

        // If player is not on full health, cancel
        if (e.player.health < 19.50) {
            e.isCancelled = true
            e.player.sendMessage(
                Component.text("You must be at full health to send a message.")
                    .color(NamedTextColor.RED)
            )
            return
        }

        var missed = false
        e.viewers().clear()
        for (player in shadow.server.onlinePlayers) {
            // check that player is not a spectator
            if (shadow.gameState.currentRoles[player.uniqueId] != PlayableRole.SPECTATOR) {
                e.viewers().add(player)
                continue
            }

            // check that player is within 34 blocks
            if (player.world != e.player.world) {
                missed = true
                continue
            }

            if (player.location.distance(e.player.location) < 24) {
                e.viewers().add(player)
            } else {
                missed = true
            }
        }

        if (missed) {
            Audience.audience(e.player).sendActionBar(
                MiniMessage.miniMessage().deserialize(
                    "<gold>Some players were too far to receive.</gold>"
                )
            )
        }
        lastChat[e.player] = System.currentTimeMillis().toDouble()
    }
}