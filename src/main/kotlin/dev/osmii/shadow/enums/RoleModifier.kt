package dev.osmii.shadow.enums

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

enum class RoleModifier(
    val icon: Material,
    val modifierName: String,
    val color: TextColor
) {
    GUESS_WHO(Material.STRUCTURE_VOID,"Guess Who", NamedTextColor.DARK_PURPLE)
}