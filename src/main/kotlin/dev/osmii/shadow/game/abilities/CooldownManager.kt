package dev.osmii.shadow.game.abilities

import dev.osmii.shadow.Shadow
import kotlin.reflect.KClass

class CooldownManager(val shadow: Shadow) {
    private var cooldownMap: HashMap<String, Cooldown> = HashMap()

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