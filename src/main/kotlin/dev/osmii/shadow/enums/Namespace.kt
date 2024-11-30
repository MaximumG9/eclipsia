package dev.osmii.shadow.enums

import dev.osmii.shadow.Shadow
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey

class Namespace(val namespace: NamespacedKey) {
    companion object {
        val FORBIDDEN = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "forbidden")
        val CUSTOM_ID = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "custom-id")

        val MODIFIER_SELECT_ADD_MODIFIER = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "add-modifier")

        val ROLE_SELECT_ADD_ROLE = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "add-role")

        val INVISIBLE = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "invisible")

        val ABILITY_SELECT_PLAYER = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "ability-select-player")
        val ABILITY_SELECT_PLAYER_PAGE = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "ability-select-player-page")
        val ABILITY_SELECT_INTERNAL_MINIMESSAGE = NamespacedKey(Bukkit.getPluginManager().getPlugin("Shadow") as Shadow, "ability-select-internal-minimessage")
    }
}