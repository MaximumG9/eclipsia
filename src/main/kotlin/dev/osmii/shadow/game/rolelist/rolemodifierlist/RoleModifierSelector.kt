package dev.osmii.shadow.game.rolelist.rolemodifierlist

import dev.osmii.shadow.enums.RoleModifier
import kotlin.math.min

class RoleModifierSelector(val modifier: RoleModifier, var chance: Int, var count: Int) {
    fun incChance(amount: Int) {
        chance += amount
        chance = min(chance,100)
    }
    fun incCount() {
        count += 1
    }
    fun decChance(amount: Int) : Boolean { // False if should be destroyed
        chance -= amount
        if(chance <= 0) return false
        else return true
    }
    fun decCount() : Boolean { // False if should be destroyed
        count -= 1
        if(count <= 0) return false
        else return true
    }
}