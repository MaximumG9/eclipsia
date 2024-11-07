package dev.osmii.shadow.events.custom.abilities

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class HandleAbilityTNTExplosion(val shadow: Shadow) : Listener {
    @EventHandler
    fun handleExplosion(e : EntityDamageEvent) {
        if (e.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            val entity = e.damageSource.directEntity?.uniqueId
            if (shadow.spawnedTNTs.stream().anyMatch { entity == it }) {
                if (e.entity is Player) {
                    val player = e.entity as Player
                    if(shadow.gameState.currentRoles.getOrDefault(player.uniqueId,PlayableRole.SPECTATOR.roleFaction) == PlayableFaction.SHADOW) {
                        e.isCancelled = true
                        player.damage(0.1)
                    }
                }
                shadow.spawnedTNTs.remove(entity)
            }
        }
    }
}