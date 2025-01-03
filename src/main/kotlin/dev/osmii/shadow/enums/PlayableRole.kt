package dev.osmii.shadow.enums

import dev.osmii.shadow.config.AbilityTestConfig
import dev.osmii.shadow.game.abilities.Ability
import dev.osmii.shadow.game.abilities.shadow.ToggleStrength
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import kotlin.reflect.KFunction0

enum class PlayableRole(
    val roleFaction: PlayableFaction,
    val roleSubfaction: PlayableSubfaction,
    val roleIcon: Material,
    val roleName: String,
    val roleDescription: String,
    val roleColor: NamedTextColor,
    val isUnique: Boolean = false,
    val abilities: List<KFunction0<Ability>>
) {
    VILLAGER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_SUPPORT, Material.EMERALD, "" +
            "Villager", "Work together to beat the game.", NamedTextColor.GREEN,
        false, listOf()
    ),
    SHERIFF(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_KILLING, Material.BOW,
        "Sheriff", "Use your bow to kill evils.", NamedTextColor.GOLD,
        false, listOf()),
    SHADOW(PlayableFaction.SHADOW, PlayableSubfaction.SHADOW_KILLING, Material.NETHERITE_SWORD,
        "Shadow", "Protect the dragon and kill the villagers.", NamedTextColor.RED,
        false, listOf(AbilityTestConfig::getShadowSecondAbility, ::ToggleStrength)),

    LIFEWEAVER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_PROTECTIVE, Material.GOLDEN_APPLE,
        "Lifeweaver", "Donate your health to others.", NamedTextColor.DARK_AQUA,
        true, listOf()),
    CORONER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_INVESTIGATIVE, Material.SHEARS,
        "Coroner", "Inspect bodies and uncover death causes.", NamedTextColor.GREEN,
        true, listOf()),
    LOOKER(PlayableFaction.VILLAGE, PlayableSubfaction.VILLAGE_SUPPORT, Material.ENDER_EYE,
        "Seer (Looker)", "Ender eyes glow", NamedTextColor.GREEN,
        true, listOf()),

    ORACLE(PlayableFaction.SHADOW, PlayableSubfaction.SHADOW_KILLING, Material.BEACON,
        "Oracle", "Kill others by guessing their role.", NamedTextColor.RED,
        true, listOf()),
    TRIDENT(PlayableFaction.SHADOW, PlayableSubfaction.SHADOW_KILLING, Material.TRIDENT,
        "Poseidon's Wrath", "Use your trident to wipe out the villagers", NamedTextColor.BLUE,
        false, listOf(::ToggleStrength)),
    SPECTATOR(PlayableFaction.SPECTATOR, PlayableSubfaction.SPECTATOR, Material.BARRIER,
        "Spectator", "Spectating.", NamedTextColor.GRAY,
        false, listOf()),

    JESTER(PlayableFaction.NEUTRAL, PlayableSubfaction.JESTER, Material.ENDER_EYE,
        "Jester", "Die to villagers without harming someone", NamedTextColor.LIGHT_PURPLE,
        true, listOf()),


}
