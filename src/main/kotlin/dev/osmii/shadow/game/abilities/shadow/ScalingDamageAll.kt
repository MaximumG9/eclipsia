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
import java.util.HashMap


class ScalingDamageAll : Ability {
    override val item: ItemStack = ItemStack(Material.NETHERITE_SWORD)



    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Damage all players within</gray> <blue>18</blue> <gray>blocks.</gray> with damage scaling with more players.</!i>")
                )
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Krill</red></!i>"))
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

        val damage = playerCountToDamageMap[targets.size]!!

        if (targets.isNotEmpty()) {
            targets.forEach {
                it.health = 0.0
                it.sendHealthUpdate()
                it.location.world.strikeLightningEffect(it.location)

                it.damage(damage)

                player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        "<red>Killed</red> <blue>${it.name}</blue><red>.</red>"
                    )
                )
            }


            TimeUtil.setCooldown(shadow, COOLDOWN_KEY, player.uniqueId.toString())
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No nearby players to kill.</red>"))
        }

    }

    companion object {
        private const val COOLDOWN = 7 * 60
        private const val INITIAL_COOLDOWN = 3 * 60
        private const val COOLDOWN_KEY = "scalingDamageKill"
        private val playerCountToDamageMap = HashMap<Int,Double>()

        init {
            playerCountToDamageMap[0] = 0.0
            playerCountToDamageMap[1] = 1.0
            playerCountToDamageMap[2] = 2.0
            playerCountToDamageMap[3] = 8.0
            playerCountToDamageMap[4] = 18.0
            playerCountToDamageMap[5] = 19.0
        }
    }
}