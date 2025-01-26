package dev.osmii.shadow.events.custom.roles

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.util.TimeUtil
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class HandleJesterHit(val shadow: Shadow) : Listener {

    @EventHandler
    fun onJesterHit(e : EntityDamageByEntityEvent) {
        var damager = e.damager
        val damagee = e.entity

        if(damager is Projectile) {
            val projectileSource = damager.shooter
            if (projectileSource is Player) {
                damager = shadow.server.getEntity(projectileSource.uniqueId)!!
            }
        }

        if(damager !is Player) return
        if(damagee !is Player) return

        if(!shadow.isRole(damager, PlayableRole.JESTER)) return

        shadow.jesterCooldowns[damager.uniqueId] = 5 * 60 * 20

        damager.sendMessage(Component.text("You can no longer win for 5 minutes").color(PlayableRole.JESTER.roleColor))
    }

    fun tick() {
        shadow.jesterCooldowns.forEach { (uuid, number) ->
            if(number == 1) {
                shadow.server.getPlayer(uuid)?.sendMessage(Component.text("You can now win again").color(PlayableRole.JESTER.roleColor))
                shadow.server.getPlayer(uuid)?.sendActionBar(Component.text("You can now win again").color(PlayableRole.JESTER.roleColor))
            }
            if(number > 0) {
                shadow.server.getPlayer(uuid)?.sendActionBar(Component.text(TimeUtil.ticksToText(number)).color(PlayableRole.JESTER.roleColor))
                shadow.jesterCooldowns[uuid] = number - 1
            }
        }
    }
}