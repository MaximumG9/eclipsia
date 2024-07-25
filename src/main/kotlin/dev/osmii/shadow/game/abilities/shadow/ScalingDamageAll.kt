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


class ScalingDamageAll : Ability {
    override val item: ItemStack = ItemStack(Material.NETHERITE_SWORD)

    private lateinit var cooldown: Cooldown

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
        if(!this::cooldown.isInitialized) cooldown = shadow.cooldownManager.getCooldown(this::class)

        val cooldownLeft = cooldown.checkCooldown(player)
        if (cooldownLeft > 0) {
            shadow.logger.info("Cooldown: $cooldownLeft")
            player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize("<red>This ability is on cooldown for</red> <blue>${TimeUtil.secondsToText(cooldownLeft)}</blue><red>.</red>")
            )
            return
        }

        val players = player.world.getNearbyPlayers(player.location, 18.0)

        var shadows = players.filter {
            (shadow.gameState.participationStatus[it.uniqueId] == true) &&
                    (shadow.gameState.currentRoles.getOrDefault(
                        it.uniqueId,
                        PlayableRole.SPECTATOR
                    ).roleFaction == PlayableFaction.SHADOW)
        }

        var targets = players

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

        val damage = if(targets.size < playerCountToDamageList.size) {
            playerCountToDamageList[targets.size]
        } else {
            playerCountToDamageList.last()
        }


        if (targets.isNotEmpty()) {
            targets.forEach {
                it.damage(damage)
                it.sendHealthUpdate()
                it.location.world.strikeLightningEffect(it.location)

                player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        "<red>Killed</red> <blue>${it.name}</blue><red>.</red>"
                    )
                )
            }

            shadows.forEach { // Fake damaging the shadows
                it.world.strikeLightningEffect(player.location)
                it.damage(0.1)
                it.sendHealthUpdate()
            }



            cooldown.resetCooldown(player)
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No nearby players to kill.</red>"))
        }

    }

    companion object {
        private val playerCountToDamageList = ArrayList<Double>()

        init {
            playerCountToDamageList.add(0.0)
            playerCountToDamageList.add(1.0)
            playerCountToDamageList.add(2.0)
            playerCountToDamageList.add(8.0)
            playerCountToDamageList.add(18.0)
            playerCountToDamageList.add(19.0)
        }
    }
}