package dev.osmii.shadow.enums

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

enum class PlayableSubfaction(
    val parentFaction: PlayableFaction,
    val factionIcon: Material,
    val color: NamedTextColor
) {
    VILLAGE_INVESTIGATIVE(PlayableFaction.VILLAGE, Material.BRUSH, NamedTextColor.GREEN),
    VILLAGE_PROTECTIVE(PlayableFaction.VILLAGE, Material.SHIELD, NamedTextColor.DARK_AQUA),
    VILLAGE_KILLING(PlayableFaction.VILLAGE, Material.IRON_SWORD, NamedTextColor.GOLD),
    VILLAGE_SUPPORT(PlayableFaction.VILLAGE, Material.IRON_PICKAXE, NamedTextColor.DARK_PURPLE),

    SHADOW_KILLING(PlayableFaction.SHADOW, Material.NETHERITE_AXE, NamedTextColor.RED),
    SHADOW_DECEPTION(PlayableFaction.SHADOW, Material.ECHO_SHARD, NamedTextColor.RED),

    JESTER(PlayableFaction.NEUTRAL, Material.TNT, NamedTextColor.LIGHT_PURPLE),

    SPECTATOR(PlayableFaction.SPECTATOR, Material.BARRIER, NamedTextColor.GRAY)
}