package dev.osmii.shadow.game.rolelist.rolemodifierlist

import dev.osmii.shadow.enums.RoleModifier
import kotlin.random.Random

class RoleModifierList {
    private val modifierMap : ArrayList<RoleModifierSelector> = arrayListOf()

    fun addModifierAtID(id: Int) {
        val selector = modifierMap[id]
        selector.incCount()
    }
    fun removeModifierAtID(id: Int) {
        val selector = modifierMap[id]
        if(!selector.decCount()) {
            modifierMap.remove(selector)
        }
    }
    fun increaseModifierChanceAtID(id: Int, delta: Int) {
        val selector = modifierMap[id]
        selector.incChance(delta)
    }
    fun decreaseModifierChanceAtID(id: Int, delta: Int) {
        val selector = modifierMap[id]
        if(!selector.decChance(delta)) {
            modifierMap.remove(selector)
        }
    }

    fun addModifier(modifier: RoleModifier) {
        val selector = modifierMap.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            modifierMap.add(RoleModifierSelector(modifier,100,1));
            return
        }
        selector.incCount()
    }

    fun removeModifier(modifier: RoleModifier) {
        val selector = modifierMap.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            return
        }
        if(!selector.decCount()) {
            modifierMap.remove(selector)
        }
    }

    fun getModifiers() : List<RoleModifierSelector> {
        return modifierMap
    }

    fun increaseModifierChance(modifier: RoleModifier, delta: Int) {
        val selector = modifierMap.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            modifierMap.add(RoleModifierSelector(modifier,100,1))
            return
        }
        selector.incChance(delta)
    }

    fun decreaseModifierChance(modifier: RoleModifier, delta: Int) {
        val selector = modifierMap.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            return
        }
        if(!selector.decChance(delta)) {
            modifierMap.remove(selector)
        }
    }

    fun pickModifiers(size : Int) : List<Set<RoleModifier>> {
        val modifiers: ArrayList<MutableSet<RoleModifier>> = arrayListOf()

        for(i in 0..<size) {
            modifiers.add(mutableSetOf())
        }

        modifierMap.forEach { selector ->
            for (i in 0..selector.count) {
                if (Random.nextInt(100) <= selector.chance) {
                    modifiers[Random.nextInt(size)].add(selector.modifier)
                }
            }
        }

        return modifiers
    }
}