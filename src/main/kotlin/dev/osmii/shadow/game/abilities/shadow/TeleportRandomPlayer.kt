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

class TeleportRandomPlayer : Ability {
    override val item: ItemStack = ItemStack(Material.WATER_BUCKET)

    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Teleport a random player within </gray><blue>18</blue> <gray>blocks of you 30 blocks above the surface</gray></!i>")
                )
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>BEGONE</red></!i>"))
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
            val target = targets.random()

            val teleportPosition = target.world.getHighestBlockAt(target.location).location
            target.teleport(teleportPosition.add(0.0,30.0,0.0))

            target.location.world.strikeLightningEffect(target.location)
            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    "<red>Teleported</red> <blue>${target.name}</blue><red>.</red>"
                )
            )

            TimeUtil.setCooldown(shadow, COOLDOWN_KEY, player.uniqueId.toString())
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No nearby players to teleport.</red>"))
        }

    }

    companion object {
        private const val COOLDOWN = 7 * 60
        private const val INITIAL_COOLDOWN = 3 * 60
        private const val COOLDOWN_KEY = "teleportnearby"
    }
}