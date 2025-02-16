package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.Cooldown
import dev.osmii.shadow.gui.PlayerSelectMenu
import dev.osmii.shadow.gui.RolelistGUI
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class GuessRole(val shadow: Shadow) : Ability {
    override val item: ItemStack = ItemStack(Material.WRITABLE_BOOK)

    override val id = "GUESS_ROLE"

    private lateinit var cooldown: Cooldown

    init {
        item.itemMeta = item.itemMeta.apply {
            this.persistentDataContainer.set(
                Namespace.ABILITY_SELECT,
                PersistentDataType.STRING,
                id
            )
            this.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Role Guess</red></!i>"))
        }
    }

    override fun apply(player: Player, shadow: Shadow): Component? {
        if(!this::cooldown.isInitialized) cooldown = shadow.cooldownManager.getCooldown(this::class)

        val cooldownLeft = cooldown.checkCooldown(player)
        if (cooldownLeft > 0) {
            shadow.logger.info("Cooldown: $cooldownLeft")
            return MiniMessage.miniMessage()
                .deserialize(
                    "<red>This ability is on cooldown for</red> <blue>${
                        TimeUtil.ticksToText(
                            cooldownLeft
                        )
                    }</blue><red>.</red>"
                )
        }

        val villagersAlive =
            shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.VILLAGE }.size
        val shadowsAlive =
            shadow.gameState.currentRoles.filter { (_, role) -> role.roleFaction == PlayableFaction.SHADOW }.size

        if(villagersAlive < shadowsAlive * 2) {
            return MiniMessage.miniMessage()
                .deserialize(
                    "<red>The number of villagers alive must be more than twice the number of shadows alive to guess.</red>"
                )
        }

        Bukkit.getScheduler().runTaskLater(shadow,Runnable {
            PlayerSelectMenu.buildMenu(
                player,
                listOf("Cancel"),
                0,
                { player -> shadow.gameState.currentRoles[player.uniqueId]?.roleFaction != PlayableFaction.SPECTATOR },
                { _, target ->
                    RolelistGUI(shadow).showGuessRoleInventory(player,{ role ->
                        role.roleFaction != shadow.gameState.currentRoles[player.uniqueId]?.roleFaction && role != PlayableRole.VILLAGER
                    }) { _, role ->
                        player.closeInventory()

                        if(shadow.isRole(target,role)) {
                            target.health = 0.0
                        } else {
                            player.health = 0.0
                        }
                    }
                },
                shadow
            )
        },2)

        return null
    }
}