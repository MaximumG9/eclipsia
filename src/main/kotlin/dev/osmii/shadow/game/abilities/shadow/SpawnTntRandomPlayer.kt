package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.inventory.ItemStack

class SpawnTntRandomPlayer : Ability {
    override val item: ItemStack = ItemStack(Material.TNT)

    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Spawn tnt on a random player within</gray> <blue>18</blue> <gray>blocks. That explodes within ${TICKS_TO_EXPLODE/20.0}</!i></gray>")
                )
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>NUKE</red></!i>"))
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

            val tnt : TNTPrimed = killed.world.spawnEntity(killed.location, EntityType.PRIMED_TNT) as TNTPrimed

            tnt.fuseTicks = TICKS_TO_EXPLODE

            killed.location.world.strikeLightningEffect(killed.location)


            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    "<red>Summoned TNT on</red> <blue>${killed.name}</blue><red>.</red>"
                )
            )

            TimeUtil.setCooldown(shadow, COOLDOWN_KEY, player.uniqueId.toString())
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No nearby players to summon TNT on.</red>"))
        }

    }

    companion object {
        private const val COOLDOWN = 7 * 60
        private const val INITIAL_COOLDOWN = 3 * 60
        var TICKS_TO_EXPLODE = 30
        private const val COOLDOWN_KEY = "spawntnt"
    }
}