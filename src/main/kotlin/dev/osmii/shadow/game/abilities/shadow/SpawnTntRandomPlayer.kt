package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SpawnTntRandomPlayer(val shadow : Shadow) : Ability {
    override val item: ItemStack = ItemStack(Material.TNT)

    override val id = "NUKE"

    private lateinit var cooldown: Cooldown

    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Spawn tnt on a random player within</gray> <blue>18</blue> <gray>blocks. That explodes within ${TimeUtil.ticksToText(shadow.config.tntExplodeTicks)}</!i>")
                )
            )
            this.persistentDataContainer.set(
                Namespace.ABILITY_SELECT,
                PersistentDataType.STRING,
                id
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>NUKE</red></!i>"))
        }
    }

    override fun apply(player: Player, shadow: Shadow) : Component {
        if(!this::cooldown.isInitialized) cooldown = shadow.cooldownManager.getCooldown(this::class)

        val cooldownLeft = cooldown.checkCooldown(player)
        if (cooldownLeft > 0) {
            shadow.logger.info("Cooldown: $cooldownLeft")
            return MiniMessage.miniMessage()
                .deserialize("<red>This ability is on cooldown for</red> <blue>${TimeUtil.ticksToText(cooldownLeft)}</blue><red>.</red>")
        }

        var targets = player.world.getNearbyPlayers(player.location, 18.0)
        targets.remove(player)
        targets = targets.filter {
            (shadow.gameState.participationStatus[it.uniqueId] == true) &&
                    !shadow.isRoleFaction(it,PlayableFaction.SPECTATOR)
        }

        if (targets.isNotEmpty()) {
            val killed = targets.random()

            val tnt : TNTPrimed = killed.world.spawnEntity(killed.location, EntityType.PRIMED_TNT) as TNTPrimed

            tnt.fuseTicks = shadow.config.tntExplodeTicks

            killed.location.world.strikeLightningEffect(killed.location)

            shadow.spawnedTNTs.add(tnt.uniqueId)

            cooldown.resetCooldown(player)

            return MiniMessage.miniMessage().deserialize(
                    "<red>Summoned TNT on</red> <blue>${killed.name}</blue><red>.</red>"
                )
        } else {
            return MiniMessage.miniMessage().deserialize("<red>No nearby players to summon TNT on.</red>")
        }
    }
}