package dev.osmii.shadow.events.custom.abilities.menu

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.Namespace
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.util.ItemUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
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

    private fun createAbilityGUI(shadow: Shadow, player: Player, abilities: List<Ability>) {
        val inventory = shadow.server.createInventory(player, InventoryType.CHEST, Component.text("Ability Menu"))

        if (abilities.count() > 4) {
            if (abilities.count() < 9) {
                abilities.forEachIndexed { index, ability ->
                    inventory.setItem(index + 9, ability.item)
                }
            } else {
                abilities.forEach {
                    inventory.addItem(it.item)
                }
            }

        } else {
            abilities.forEachIndexed { index, ability ->
                inventory.setItem(floor(9 * ((index + 1.0) / (abilities.count() + 1)) + 9).toInt(), ability.item)
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

        val ability = playerAbilityHashMap[e.whoClicked.uniqueId]!!.find {
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
            e.whoClicked.setItemOnCursor(null)
            return
        }

        ability.apply(e.whoClicked as Player,shadow)

        e.isCancelled = true
        e.whoClicked.setItemOnCursor(null)

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            e.whoClicked.closeInventory()
        }, 1)
    }
}