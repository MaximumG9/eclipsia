package dev.osmii.shadow.enums

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

enum class PlayableRole(
    val roleFaction: PlayableFaction,
    val roleSubfaction: PlayableSubfaction,
    val roleIcon: Material,
    val roleName: String,
    val roleDescription: String,
    val roleColor: NamedTextColor,
    val isUnique: Boolean = false,
) {
    VILLAGER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_SUPPORT, Material.EMERALD, "" +
            "Villager", "Work together to beat the game.", NamedTextColor.GREEN,
        false),
    SHERIFF(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_KILLING, Material.BOW,
        "Sheriff", "Use your bow to kill evils.", NamedTextColor.GOLD,
        false),
    SHADOW(PlayableFaction.SHADOW, PlayableSubfaction.SHADOW_KILLING, Material.NETHERITE_SWORD,
        "Shadow", "Protect the dragon and kill the villagers.", NamedTextColor.RED,
        false),

    LIFEWEAVER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_PROTECTIVE, Material.GOLDEN_APPLE,
        "Lifeweaver", "Donate your health to others.", NamedTextColor.DARK_AQUA,
        true),
    CORONER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_INVESTIGATIVE, Material.SHEARS,
        "Coroner", "Inspect bodies and uncover death causes.", NamedTextColor.GREEN,
        true),

    ORACLE(PlayableFaction.SHADOW, PlayableSubfaction.SHADOW_KILLING, Material.ENDER_EYE,
        "Oracle", "Kill others by guessing their role.", NamedTextColor.RED,
        true),
    TRIDENT(PlayableFaction.SHADOW, PlayableSubfaction.SHADOW_KILLING, Material.TRIDENT,
        "Poseidon's Wrath", "Use your trident to wipe out the villagers", NamedTextColor.BLUE,
        true),
    SPECTATOR(PlayableFaction.SPECTATOR, PlayableSubfaction.SPECTATOR, Material.BARRIER,
        "Spectator", "Spectating.", NamedTextColor.GRAY,
        false),


}
