package dev.osmii.shadow.events.custom.abilities.item

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class HandleRightClickHeart(val shadow: Shadow) : Listener {

    @EventHandler
    fun onRightClickHeart(e: PlayerInteractEvent) {
        if (e.item == null || !ItemUtil.customIdIs(e.item!!, CID.HEART)) return

        val player = e.player

        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!

        val possibleRemovalModifier = maxHealth.modifiers.filter { modifier ->
            modifier.amount == -2.0 && modifier.operation == AttributeModifier.Operation.ADD_NUMBER
        }.firstOrNull()

        if(possibleRemovalModifier != null) {
            maxHealth.removeModifier(possibleRemovalModifier)
            return
        } else {
            maxHealth.addModifier(AttributeModifier("addHeart",2.0,AttributeModifier.Operation.ADD_NUMBER))
        }
    }
}