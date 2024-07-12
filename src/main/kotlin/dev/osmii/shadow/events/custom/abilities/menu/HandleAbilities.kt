package dev.osmii.shadow.events.custom.abilities.menu

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.config.AbilityTestConfig
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.ToggleStrength
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
import org.bukkit.inventory.ItemStack
import kotlin.math.floor

class HandleAbilities(val shadow: Shadow) : Listener {
    var inventories: MutableList<Inventory> = ArrayList()

    var abilityHashMap: HashMap<ItemStack, Ability> = HashMap()

    private fun registerAbility(ability: Ability) {
        abilityHashMap.put(ability.item, ability)
    }

    private fun createAbilityGUI(shadow: Shadow, player: Player, abilities: List<Ability>) {
        val inventory = shadow.server.createInventory(player, InventoryType.CHEST, Component.text("Ability Menu"))
        if (abilities.count() > 4) {
            if (abilities.count() < 9) {
                abilities.forEachIndexed { index, ability ->
                    if (!abilityHashMap.containsValue(ability)) registerAbility(ability)
                    inventory.setItem(index + 9, ability.item)
                }
            } else {
                abilities.forEach {
                    if (!abilityHashMap.containsValue(it)) registerAbility(it)
                    inventory.addItem(it.item)
                }
            }

        } else {
            abilities.forEachIndexed { index, ability ->
                if (!abilityHashMap.containsValue(ability)) registerAbility(ability)
                inventory.setItem(floor(9 * ((index + 1.0) / (abilities.count() + 1)) + 9).toInt(), ability.item)
            }
        }
        player.openInventory(inventory)
        inventories.add(inventory)
    }

    @EventHandler
    fun onOpenAbilityMenu(e: PlayerInteractEvent) {
        if (e.item == null || !ItemUtil.customIdIs(e.item!!, CID.HOTBAR_ABILITY_SELECT)) return
        val abilityList: MutableList<Ability> = ArrayList()
        if (shadow.gameState.currentRoles[e.player.uniqueId] == PlayableRole.SHADOW) {
            abilityList.add(ToggleStrength())
            abilityList.add(AbilityTestConfig.getShadowSecondAbility())
        }
        createAbilityGUI(shadow, e.player, abilityList)
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
        abilityHashMap[e.currentItem]?.apply(e.whoClicked as Player, shadow)

        e.isCancelled = true
        e.whoClicked.setItemOnCursor(null)

        Bukkit.getScheduler().runTaskLater(shadow, Runnable {
            e.whoClicked.closeInventory()
        }, 1)
    }
}