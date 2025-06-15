package dev.osmii.shadow.events.custom.abilities

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class HandleAbilityTNTExplosion(val shadow: Shadow) : Listener {
    @EventHandler
    fun handleExplosion(e : EntityDamageEvent) {
        if (e.entity is Player) {
            if (e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                val entity = e.damageSource.directEntity?.uniqueId
                if (shadow.spawnedTNTs.stream().anyMatch { entity == it }) {

                    val player = e.entity as Player
                    if (shadow.isRoleFaction(player, PlayableFaction.SHADOW)) {
                        e.isCancelled = true
                        player.damage(0.1)
                    }
                } }
        }
    }

    @EventHandler
    fun handleTntDisappear(e : EntityRemoveFromWorldEvent) {
        shadow.spawnedTNTs.remove(e.entity.uniqueId);
    }
}