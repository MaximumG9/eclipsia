package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.Cooldown
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


class ScalingDamageAll(val shadow: Shadow) : Ability {
    override val item: ItemStack = ItemStack(Material.NETHERITE_SWORD)

    override val id = "CULL"

    private lateinit var cooldown: Cooldown
    
    private var hasActivatedStrengthTonight = false

    init {
        item.itemMeta = item.itemMeta.apply {
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Damage all players within</gray> <blue>18</blue> <gray>blocks.</gray> with damage scaling with more players.</!i>")
                )
            )
            this.persistentDataContainer.set(
                Namespace.ABILITY_SELECT,
                PersistentDataType.STRING,
                id
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Cull</red></!i>"))
        }
    }

    override fun apply(player: Player, shadow: Shadow) : Component {
        if(shadow.config.cullNightly) {
            if(shadow.overworld.isDayTime) {
                return MiniMessage.miniMessage()
                    .deserialize("<red>This ability cannot be used during daytime.</red>")
            }
            if(this.hasActivatedStrengthTonight) {
                return MiniMessage.miniMessage()
                    .deserialize("<red>This ability has already been activated tonight</red>")
            }
            this.hasActivatedStrengthTonight = true
        } else {
            if(!this::cooldown.isInitialized) cooldown = shadow.cooldownManager.getCooldown(this::class)

            val cooldownLeft = cooldown.checkCooldown(player)
            if (cooldownLeft > 0) {
                shadow.logger.info("Cooldown: $cooldownLeft")
                return MiniMessage.miniMessage()
                    .deserialize("<red>This ability is on cooldown for</red> <blue>${TimeUtil.ticksToText(cooldownLeft)}</blue><red>.</red>")
            }
        }

        
        val players = player.world.getNearbyPlayers(player.location, 18.0)

        val shadows = players.filter {
            (shadow.gameState.participationStatus[it.uniqueId] == true) &&
                    shadow.isRoleFaction(it,PlayableFaction.SHADOW)
        }

        var targets = players

        targets.remove(player)
        targets = targets.filter {
            ((shadow.gameState.participationStatus[it.uniqueId] == true) &&
                    !shadow.isRoleFaction(it,PlayableFaction.SHADOW)) &&
                    !shadow.isRoleFaction(it,PlayableFaction.SPECTATOR)

        }

        val damage = if(targets.size < playerCountToDamageList.size) {
            playerCountToDamageList[targets.size]
        } else {
            playerCountToDamageList.last()
        }


        if (targets.isNotEmpty()) {
            val returnComponent = Component.text("")

            targets.forEach {
                it.damage(damage)
                it.sendHealthUpdate()
                it.location.world.strikeLightningEffect(it.location)

                returnComponent.append(
                    MiniMessage.miniMessage().deserialize(
                        "<red>Hit</red> <blue>${it.name}</blue><red>.</red>"
                    )
                )
                returnComponent.appendNewline()
            }

            shadows.forEach { // Fake damaging the shadows
                it.world.strikeLightningEffect(player.location)
                it.damage(0.1)
                it.sendHealthUpdate()
            }
            if(!shadow.config.cullNightly) {
                cooldown.resetCooldown(player)
            }
            return returnComponent
        } else {
            return MiniMessage.miniMessage().deserialize("<red>No nearby players to hit.</red>")
        }

    }

    companion object {
        private val playerCountToDamageList = ArrayList<Double>()

        init {
            playerCountToDamageList.add(0.0)
            playerCountToDamageList.add(2.0)
            playerCountToDamageList.add(6.0)
            playerCountToDamageList.add(12.0)
            playerCountToDamageList.add(19.0)
        }
    }
}