package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.game.abilities.Ability
import kotlin.reflect.KClass

class CooldownManager(val shadow: Shadow) {
    var cooldownMap: HashMap<String,Cooldown> = HashMap()

    fun getCooldown(clazz: KClass<out Ability>) : Cooldown {
        val name = clazz.qualifiedName!!
        var cooldown = cooldownMap[name]
        if(cooldown != null) {
            return cooldown
        } else {
            cooldown = Cooldown(shadow)
            cooldownMap[name] = cooldown
            return cooldown
        }
    }
}