package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.Cooldown
import dev.osmii.shadow.util.ItemUtil
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SummonPoisonCloud(var shadow: Shadow) : Ability {
    override val item: ItemStack = ItemStack(Material.POTION)

    override val id = "POISONBURST"

    private lateinit var cooldown: Cooldown

    init {
        item.itemMeta = (item.itemMeta as PotionMeta).apply {
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Poison Burst</red></!i>"))
            this.lore(
                listOf(
                    MiniMessage.miniMessage()
                        .deserialize("<!i><gray>Create a cloud of</gray> <blue>Poison!?</blue> <gray>.</gray></!i>")
                )
            )
            this.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            this.addCustomEffect(
                PotionEffect(
                    PotionEffectType.POISON,
                    0,
                    0,
                    false,
                    false,
                    true
                ), true
            )
            this.persistentDataContainer.set(
                Namespace.ABILITY_SELECT,
                PersistentDataType.STRING,
                id
            )
            this.persistentDataContainer.set(
                Namespace.FORBIDDEN,
                PersistentDataType.BYTE_ARRAY,
                ItemUtil.forbidden(drop = true, use = false, move = false, moveContainer = false)
            )
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

        cooldown.resetCooldown(player)

        if (targets.isNotEmpty()) {
            val target = targets.random()

            shadow.poisonClouds.add(PoisonCloud(shadow,target.location))

            return MiniMessage.miniMessage().deserialize(
                    "<red>Summoned poison on</red> <blue>${target.name}</blue><red>.</red>"
                )
        } else {
            shadow.poisonClouds.add(PoisonCloud(shadow,player.location))

            return MiniMessage.miniMessage().deserialize(
                    "<red>Summoned poison on</red> <blue>you</blue><red>.</red>"
                )
        }
    }
}