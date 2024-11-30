package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class TeleportRandomPlayer : Ability {
    override val item: ItemStack = ItemStack(Material.WATER_BUCKET)
    override val id = "BEGONE"

    private lateinit var cooldown: Cooldown

    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Teleport a random player within </gray><blue>18</blue> <gray>blocks of you 30 blocks above the surface</gray></!i>")
                )
            )
            this.persistentDataContainer.set(
                Namespace.ABILITY_SELECT,
                PersistentDataType.STRING,
                id
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>BEGONE</red></!i>"))
        }
    }

    override fun apply(player: Player, shadow: Shadow) {
        if(!this::cooldown.isInitialized) cooldown = shadow.cooldownManager.getCooldown(this::class)

        val cooldownLeft = cooldown.checkCooldown(player)
        if (cooldownLeft > 0) {
            shadow.logger.info("Cooldown: $cooldownLeft")
            player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("<red>This ability is on cooldown for</red> <blue>${TimeUtil.ticksToText(cooldownLeft)}</blue><red>.</red>")
            )
            return
        }

        var targets = player.world.getNearbyPlayers(player.location, 18.0)
        targets.remove(player)
        targets = targets.filter {
            (shadow.gameState.participationStatus[it.uniqueId] == true) &&
                    !shadow.isRoleFaction(it,PlayableFaction.SHADOW) &&
                    !shadow.isRoleFaction(it,PlayableFaction.SHADOW)

        }

        if (targets.isNotEmpty()) {
            val target = targets.random()

            val teleportPosition = target.world.getHighestBlockAt(target.location).location
            target.teleport(teleportPosition.add(0.0, HEIGHT_ABOVE_GROUND,0.0))

            target.location.world.strikeLightningEffect(target.location)
            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    "<red>Teleported</red> <blue>${target.name}</blue><red>.</red>"
                )
            )

            cooldown.resetCooldown(player)
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No nearby players to teleport.</red>"))
        }

    }

    companion object {
        var HEIGHT_ABOVE_GROUND = 30.0
    }
}