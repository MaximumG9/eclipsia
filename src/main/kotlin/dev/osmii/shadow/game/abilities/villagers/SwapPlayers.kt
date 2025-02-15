package dev.osmii.shadow.game.abilities.villagers

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.Cooldown
import dev.osmii.shadow.gui.PlayerSelectMenu
import dev.osmii.shadow.util.ItemUtil
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.time.Duration

class SwapPlayers(var shadow: Shadow) : Ability {
    override val id = "swap-players"
    override val item: ItemStack = ItemStack(Material.ENDER_PEARL)

    init {
        item.itemMeta = item.itemMeta.apply {
            this.displayName(Component.text("Swap Players").color(NamedTextColor.BLUE))
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

    private lateinit var cooldown: Cooldown

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

        Bukkit.getScheduler().runTaskLater(shadow,Runnable {
            PlayerSelectMenu.buildMenu(
                player,
                listOf("Cancel"),
                0,
                { player -> shadow.gameState.currentRoles[player.uniqueId]?.roleFaction != PlayableFaction.SPECTATOR },
                { _, firstTarget ->
                    PlayerSelectMenu.buildMenu(
                        player,
                        listOf("Cancel"),
                        0,
                        { player -> shadow.gameState.currentRoles[player.uniqueId]?.roleFaction != PlayableFaction.SPECTATOR && player != firstTarget },
                        { _, secondTarget ->
                            val times = Title.Times.times(Duration.ofSeconds(0),Duration.ofSeconds(1),Duration.ofMillis(250))
                            firstTarget.showTitle(Title.title(
                                Component.text("Swapping In:").color(NamedTextColor.DARK_PURPLE),
                                Component.text("3 Seconds").color(NamedTextColor.DARK_BLUE),
                                times
                            ))
                            secondTarget.showTitle(Title.title(
                                Component.text("Swapping In:").color(NamedTextColor.DARK_PURPLE),
                                Component.text("3 Seconds").color(NamedTextColor.DARK_BLUE),
                                times
                            ))
                            Bukkit.getScheduler().runTaskLater(shadow,Runnable {
                                firstTarget.showTitle(Title.title(
                                    Component.text("3").color(NamedTextColor.GREEN),
                                    Component.empty(),
                                    times
                                ))
                                secondTarget.showTitle(Title.title(
                                    Component.text("3").color(NamedTextColor.GREEN),
                                    Component.empty(),
                                    times
                                ))
                            },20)
                            Bukkit.getScheduler().runTaskLater(shadow,Runnable {
                                firstTarget.showTitle(Title.title(
                                    Component.text("2").color(NamedTextColor.YELLOW),
                                    Component.empty(),
                                    times
                                ))
                                secondTarget.showTitle(Title.title(
                                    Component.text("2").color(NamedTextColor.YELLOW),
                                    Component.empty(),
                                    times
                                ))
                            },40)
                            Bukkit.getScheduler().runTaskLater(shadow,Runnable {
                                firstTarget.showTitle(Title.title(
                                    Component.text("1").color(NamedTextColor.RED),
                                    Component.empty(),
                                    times
                                ))
                                secondTarget.showTitle(Title.title(
                                    Component.text("1").color(NamedTextColor.GREEN),
                                    Component.empty(),
                                    times
                                ))
                            },60)
                            Bukkit.getScheduler().runTaskLater(shadow,Runnable {
                                val tempLocation = firstTarget.location
                                firstTarget.teleport(secondTarget)
                                secondTarget.teleport(tempLocation)
                            },80)
                        },
                        shadow
                    )
                },
                shadow
            )
        },2)
        return null
    }
}