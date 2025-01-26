package dev.osmii.shadow.enums

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

enum class RoleModifier(
    val icon: Material,
    val modifierName: String,
    val color: TextColor
) {
    GUESS_WHO(Material.STRUCTURE_VOID,"Guess Who", NamedTextColor.DARK_PURPLE),
    THE_HEDGEHOG(Material.LINGERING_POTION,"Shadow The Hedgehog", NamedTextColor.BLACK),
    QUICK_START(Material.STONE_PICKAXE,"Quick Start", NamedTextColor.GREEN)
}