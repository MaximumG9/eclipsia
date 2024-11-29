package dev.osmii.shadow.events.custom.abilities.item.trident

import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class HandleTridentCooldown : Listener {
    @EventHandler
    fun onMelee(e : EntityDamageByEntityEvent) {
        val damager = e.damager
        if(damager !is Player) return
        if(!(e.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
        e.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return
        if(!ItemUtil.customIdIs(damager.inventory.itemInMainHand, CID.TRIDENT)) return
        if(damager.getCooldown(Material.TRIDENT) > 0) {
            e.isCancelled = true
            return
        }

        val damagee = e.entity
        if(damagee !is Player) return
        if(!damagee.isBlocking) return
        if(damagee.activeItem.type != Material.SHIELD) return

        damagee.setCooldown(Material.SHIELD,100)
        damagee.clearActiveItem()
    }
}