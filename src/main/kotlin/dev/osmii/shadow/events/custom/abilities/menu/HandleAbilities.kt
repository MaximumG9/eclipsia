package dev.osmii.shadow.events.custom.abilities.menu

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.enums.RoleModifier
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.NoopAbility
import dev.osmii.shadow.util.ItemUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.math.floor

class HandleAbilities(val shadow: Shadow) : Listener {
    private var inventories: MutableList<Inventory> = ArrayList()

    private var playerAbilityHashMap: HashMap<UUID, ArrayList<Ability>> = HashMap()

    fun clear() {
        playerAbilityHashMap.clear()
        inventories.clear()
    }

    fun getAbilities(player: Player) : ArrayList<Ability>? {
        return playerAbilityHashMap[player.uniqueId]
    }

    private fun createAbilityGUI(shadow: Shadow, player: Player, abilities: ArrayList<Ability>) {
        val inventory = shadow.server.createInventory(player, InventoryType.CHEST, Component.text("Ability Menu"))

        if(shadow.hasRoleModifier(player,RoleModifier.GUESS_WHO)) {
            while (abilities.size < 3) {
                abilities.add(NoopAbility())
            }
            abilities.shuffle()
        }


        if (abilities.count() > 4) {
            if (abilities.count() < 9) {
                abilities.forEachIndexed { index, ability ->
                    if(shadow.hasRoleModifier(player,RoleModifier.GUESS_WHO)) {
                        val replacementItem = ItemStack(Material.JIGSAW)
                        replacementItem.itemMeta = replacementItem.itemMeta.apply {
                            this.persistentDataContainer.set(
                                Namespace.ABILITY_SELECT,
                                PersistentDataType.STRING,
                                ability.id
                            )
                        }
                        inventory.setItem(index + 9, replacementItem)
                    } else {
                        inventory.setItem(index + 9, ability.item)
                    }
                }
            } else {
                abilities.forEach { ability ->
                    if(shadow.hasRoleModifier(player,RoleModifier.GUESS_WHO)) {
                        val replacementItem = ItemStack(Material.JIGSAW)
                        replacementItem.itemMeta = replacementItem.itemMeta.apply {
                            this.persistentDataContainer.set(
                                Namespace.ABILITY_SELECT,
                                PersistentDataType.STRING,
                                ability.id
                            )
                        }
                        inventory.addItem(replacementItem)
                    } else {
                        inventory.addItem(ability.item)
                    }
                }
            }

        } else {
            abilities.forEachIndexed { index, ability ->
                if(shadow.hasRoleModifier(player,RoleModifier.GUESS_WHO)) {
                    val replacementItem = ItemStack(Material.JIGSAW)
                    replacementItem.itemMeta = replacementItem.itemMeta.apply {
                        this.persistentDataContainer.set(
                            Namespace.ABILITY_SELECT,
                            PersistentDataType.STRING,
                            ability.id
                        )
                    }
                    inventory.setItem(floor(9 * ((index + 1.0) / (abilities.count() + 1)) + 9).toInt(), replacementItem)
                } else {
                    inventory.setItem(floor(9 * ((index + 1.0) / (abilities.count() + 1)) + 9).toInt(), ability.item)
                }
            }
        }
        player.openInventory(inventory)
        inventories.add(inventory)
    }

    @EventHandler
    fun onOpenAbilityMenu(e: PlayerInteractEvent) {
        if (e.item == null || !ItemUtil.customIdIs(e.item!!, CID.HOTBAR_ABILITY_SELECT)) return

        val player = e.player

        if(playerAbilityHashMap[player.uniqueId] == null || playerAbilityHashMap[player.uniqueId]?.isEmpty() == true) {
            playerAbilityHashMap[player.uniqueId] = arrayListOf()
            shadow.gameState.currentRoles[player.uniqueId]?.abilities?.forEach {
                playerAbilityHashMap[player.uniqueId]!!.add(it.invoke())
            }
        }

        createAbilityGUI(shadow, e.player, playerAbilityHashMap[player.uniqueId]!!)
    }

    @EventHandler
    fun onCloseAbilityGUI(e: InventoryCloseEvent) {
        if (inventories.contains(e.inventory)) inventories.remove(e.inventory)
    }

    @EventHandler
    fun onActivateAbility(e: InventoryClickEvent) {
        if (!inventories.contains(e.inventory)) return
        if (!inventories.contains(e.clickedInventory)) {
            e.isCancelled = true
            return
        }

        val player = e.whoClicked as Player

        val ability = playerAbilityHashMap[player.uniqueId]!!.find {
            e.currentItem?.let { it1 ->
                ItemUtil.customKeyIs(
                    Namespace.ABILITY_SELECT,
                    it1,
                    it.id
                )
            } == true
        }

        if(ability == null) {
            e.isCancelled = true
            player.setItemOnCursor(null)
            return
        }

        val returnText = ability.apply(player,shadow)

        if(!shadow.hasRoleModifier(player,RoleModifier.GUESS_WHO))
            player.sendMessage(returnText)

        e.isCancelled = true
        player.setItemOnCursor(null)

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            player.closeInventory()
        }, 1)
    }
}