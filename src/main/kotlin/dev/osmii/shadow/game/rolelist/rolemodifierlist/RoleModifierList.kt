package dev.osmii.shadow.game.rolelist.rolemodifierlist

import dev.osmii.shadow.enums.RoleModifier
import kotlin.random.Random

class RoleModifierList {
    private val modifiers : ArrayList<RoleModifierSelector> = arrayListOf()

    fun addModifierAtID(id: Int) {
        val selector = modifiers[id]
        selector.incCount()
    }
    fun removeModifierAtID(id: Int) {
        val selector = modifiers[id]
        if(!selector.decCount()) {
            modifiers.remove(selector)
        }
    }
    fun increaseModifierChanceAtID(id: Int, delta: Int) {
        val selector = modifiers[id]
        selector.incChance(delta)
    }
    fun decreaseModifierChanceAtID(id: Int, delta: Int) {
        val selector = modifiers[id]
        if(!selector.decChance(delta)) {
            modifiers.remove(selector)
        }
    }

    fun addModifier(modifier: RoleModifier) {
        val selector = modifiers.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            modifiers.add(RoleModifierSelector(modifier,100,1));
            return
        }
        selector.incCount()
    }

    fun removeModifier(modifier: RoleModifier) {
        val selector = modifiers.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            return
        }
        if(!selector.decCount()) {
            modifiers.remove(selector)
        }
    }

    fun getModifiers() : List<RoleModifierSelector> {
        return modifiers
    }

    fun increaseModifierChance(modifier: RoleModifier, delta: Int) {
        val selector = modifiers.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            modifiers.add(RoleModifierSelector(modifier,100,1))
            return
        }
        selector.incChance(delta)
    }

    fun decreaseModifierChance(modifier: RoleModifier, delta: Int) {
        val selector = modifiers.find { selector -> selector.modifier == modifier }
        if(selector == null) {
            return
        }
        if(!selector.decChance(delta)) {
            modifiers.remove(selector)
        }
    }

    fun pickModifiers(size : Int) : List<Set<RoleModifier>> {
        val modifiers: ArrayList<MutableSet<RoleModifier>> = arrayListOf()

        for(i in 0..<size) {
            modifiers.add(mutableSetOf())
        }

        this.modifiers.forEach { selector ->
            for (i in 0..<selector.count) {
                if (Random.nextInt(100) <= selector.chance) {
                    modifiers[Random.nextInt(size)].add(selector.modifier)
                }
            }
        }

        return modifiers
    }
}