package dev.osmii.shadow.events.custom.abilities.item.sheriff

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

class HandleSheriffBow(val shadow: Shadow) : Listener {

    private var sheriffArrows: HashMap<Int, ItemStack> = HashMap()
    private var sheriffs: HashMap<Int, Player> = HashMap()

    @EventHandler
    fun onShoot(e: EntityShootBowEvent) {
        if (e.bow == null) return
        if (!ItemUtil.customIdIs(e.bow!!, CID.INVENTORY_SHERIFF_BOW)) return
        if (e.entity !is Player) return

        e.projectile.isGlowing = true
        sheriffArrows[e.projectile.entityId] = e.bow!!
        sheriffs[e.projectile.entityId] = e.entity as Player
    }

    @EventHandler
    fun preOnHit(e: ProjectileHitEvent) {
        if(e.entity.type != EntityType.ARROW) return
        if (e.entity.entityId !in sheriffArrows.keys) return
        if (sheriffs[e.entity.entityId] == null || sheriffs[e.entity.entityId]?.isOnline == false) return
        val entity = e.hitEntity
        if(entity is Player && entity.isBlocking && entity.activeItem.type == Material.SHIELD) {
            entity.setCooldown(Material.SHIELD,100)
        }
    }

    @EventHandler
    fun onHit(e: EntityDamageByEntityEvent) {
        if (e.damager.entityId !in sheriffArrows.keys) return
        if (sheriffs[e.damager.entityId] == null || sheriffs[e.damager.entityId]?.isOnline == false) return


        e.damage = 1000.0

        if (e.entityType == EntityType.ENDER_DRAGON || e.entityType == EntityType.WITHER) {
            // Disable damage to boss entities
            e.damage = 0.0
            sheriffs[e.damager.entityId]?.world?.strikeLightning(sheriffs[e.damager.entityId]!!.location)
        }

        // Break bow
        val bow = sheriffArrows[e.damager.entityId]
        sheriffs[e.damager.entityId]?.playSound(
            sheriffs[e.damager.entityId]!!.location,
            Sound.ITEM_SHIELD_BREAK,
            1.0f,
            1.0f
        )

        bow?.amount = 0
    }
}