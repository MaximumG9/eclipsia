package dev.osmii.shadow.events.custom.abilities.item.trident

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class HandleTrident(val shadow: Shadow) : Listener {

    @EventHandler
    fun onShoot(e: PlayerLaunchProjectileEvent) {
        if (!ItemUtil.customIdIs(e.itemStack, CID.TRIDENT)) return

        shadow.server.onlinePlayers.forEach { player ->
            if(!(shadow.gameState.currentRoles[player.uniqueId]?.roleFaction == PlayableFaction.SHADOW ||
                shadow.gameState.currentRoles[player.uniqueId]?.roleFaction == PlayableFaction.SPECTATOR)) {
                player.hideEntity(shadow,e.projectile)
            }
        }
    }

    @EventHandler
    fun preOnHit(e: ProjectileHitEvent) {
        if(e.entity !is Trident) return
        val player = e.hitEntity

        if(player is Player && player.isBlocking && player.activeItem.type == Material.SHIELD) {
            player.setCooldown(Material.SHIELD,100)
        }
    }
}