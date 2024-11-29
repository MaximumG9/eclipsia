package dev.osmii.shadow.events.custom.abilities.item.trident

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.util.ItemUtil
import net.minecraft.world.damagesource.DamageTypes
import org.bukkit.Material
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffectType

class HandleTrident(val shadow: Shadow) : Listener {

    @EventHandler
    fun onShoot(e: PlayerLaunchProjectileEvent) {
        if (!ItemUtil.customIdIs(e.itemStack, CID.TRIDENT)) return

        shadow.server.onlinePlayers.forEach { player ->
            if(!(shadow.isRoleFaction(e.player,PlayableFaction.SHADOW) ||
                        shadow.isRoleFaction(e.player,PlayableFaction.SPECTATOR))) {
                player.hideEntity(shadow,e.projectile)
            }
        }
    }

    @EventHandler
    fun preOnHit(e: ProjectileHitEvent) {
        val entity = e.entity
        if(entity !is Trident) return
        if(!ItemUtil.customIdIs(entity.itemStack,CID.TRIDENT)) return
        val player = e.hitEntity

        if(player is Player && player.isBlocking && player.activeItem.type == Material.SHIELD) {
            player.setCooldown(Material.SHIELD,100)
            player.clearActiveItem()
        }
    }

    @EventHandler
    fun onHit(e: EntityDamageByEntityEvent) {
        val damager = e.damager
        if(damager !is Player) return
        if(e.damageSource.damageType != DamageType.TRIDENT) return
        val trident = e.damageSource.directEntity
        if(trident !is Trident) return
        if(!ItemUtil.customIdIs(trident.itemStack,CID.TRIDENT)) return


        e.damage = damager.getPotionEffect(PotionEffectType.INCREASE_DAMAGE)
            ?.amplifier
            ?.plus(1)
            ?.times(3)
            ?.plus(e.damage)
            ?: e.damage // Increases projectile damage with strength
    }
}