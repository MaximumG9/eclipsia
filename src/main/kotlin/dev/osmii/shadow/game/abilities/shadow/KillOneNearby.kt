package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class KillOneNearby : Ability {
    override val item: ItemStack = ItemStack(Material.NETHERITE_SWORD)

    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Instantly kill the nearest player within</gray> <blue>18</blue> <gray>blocks.</gray></!i>")
                )
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Assassinate</red></!i>"))
        }
    }

    override fun apply(player: Player, shadow: Shadow) {
        val cooldown =
            TimeUtil.checkCooldown(shadow, COOLDOWN, INITIAL_COOLDOWN, COOLDOWN_KEY, player.uniqueId.toString())
        if (cooldown > 0) {
            shadow.logger.info("Cooldown: $cooldown")
            player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("<red>This ability is on cooldown for</red> <blue>${TimeUtil.secondsToText(cooldown)}</blue><red>.</red>")
            )
            return
        }

        var targets = player.world.getNearbyPlayers(player.location, 18.0)
        targets.remove(player)
        targets = targets.filter {
            (shadow.gameState.participationStatus[it.uniqueId] == true) &&
                    (shadow.gameState.currentRoles.getOrDefault(
                        it.uniqueId,
                        PlayableRole.SPECTATOR
                    ).roleFaction != PlayableFaction.SHADOW) &&
                    shadow.gameState.currentRoles.getOrDefault(
                        it.uniqueId,
                        PlayableRole.SPECTATOR.roleFaction
                    ) != PlayableFaction.SPECTATOR

        }

        if (targets.isNotEmpty()) {
            val killed = targets.random()
            killed.health = 0.0
            killed.sendHealthUpdate()
            killed.location.world.strikeLightningEffect(killed.location)
            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    "<red>Killed</red> <blue>${killed.name}</blue><red>.</red>"
                )
            )

            TimeUtil.setCooldown(shadow, COOLDOWN_KEY, player.uniqueId.toString())
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No nearby players to kill.</red>"))
        }

    }

    companion object {
        private const val COOLDOWN = 7 * 60
        private const val INITIAL_COOLDOWN = 3 * 60
        private const val COOLDOWN_KEY = "singlekillnearby"
    }
}