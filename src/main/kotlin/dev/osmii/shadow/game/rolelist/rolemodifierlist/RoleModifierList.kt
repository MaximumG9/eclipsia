package dev.osmii.shadow.game.rolelist.rolemodifierlist

import dev.osmii.shadow.enums.RoleModifier
import java.util.*
import java.util.Collections.fill
import kotlin.random.Random

class RoleModifierList {
    private val modifierMap : EnumMap<RoleModifier,Int> = EnumMap(RoleModifier::class.java)

    fun addModifier(modifier: RoleModifier) {
        if(modifierMap.containsKey(modifier)) return
        modifierMap[modifier] = 100
    }

    fun removeModifier(modifier: RoleModifier) {
        modifierMap.remove(modifier)
    }

    fun increaseModifierChance(modifier: RoleModifier, delta: Int) {
        if(modifierMap[modifier] == null) addModifier(modifier)
        modifierMap[modifier] = modifierMap[modifier]!! + delta
        if(modifierMap[modifier]!! > 100) {
            modifierMap[modifier] = 100
        }
    }

    fun decreaseModifierChance(modifier: RoleModifier, delta: Int) {
        if(modifierMap[modifier] == null) addModifier(modifier)
        modifierMap[modifier] = modifierMap[modifier]!! - delta
        if(modifierMap[modifier]!! <= 0) {
            removeModifier(modifier)
        }
    }

    fun pickModifiers() : List<Set<RoleModifier>> {
        val modifiers: ArrayList<Set<RoleModifier>> = arrayListOf()

        TODO()

        modifierMap.forEach({(modifier, chance) ->
            if(Random.nextInt(0,100) < chance) {

            }
        });
        return modifiers
    }
}