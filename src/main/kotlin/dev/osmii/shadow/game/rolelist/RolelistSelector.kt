package dev.osmii.shadow.game.rolelist

import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.enums.PlayableSubfaction
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

class RolelistSelector : Comparable<RolelistSelector> {

    val roles = ArrayList<PlayableRole>()
    val mutableRoles = ArrayList<PlayableRole>()
    var specificity = 0
    var selectorText: Component? = null

    private var faction: PlayableFaction? = null
    private var subfaction: PlayableSubfaction? = null
    var role: PlayableRole? = null

    constructor(e: PlayableRole) {
        roles.add(e)
        selectorText = Component.text(e.roleName).color(e.roleColor).decoration(TextDecoration.ITALIC, false)
        specificity = 3

        faction = e.roleFaction
        subfaction = e.roleSubfaction
        role = e
    }

    constructor(e: PlayableSubfaction) {
        PlayableRole.entries.forEach { role ->
            if (role.roleSubfaction == e) {
                roles.add(role)
            }
        }

        val subfactionName = e.name.split("_").joinToString(" ")
        val left = subfactionName.split(" ").first().lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val right = subfactionName.split(" ").last().lowercase(Locale.getDefault())
        selectorText = Component.text(left).color(e.color)
            .append(Component.text(right).color(NamedTextColor.BLUE))
        specificity = 2

        faction = e.parentFaction
        subfaction = e
    }

    constructor(e: PlayableFaction) {
        PlayableRole.entries.forEach { role ->
            if (role.roleFaction == e) {
                roles.add(role)
            }
        }
        selectorText = Component.text("Random ").color(NamedTextColor.BLUE)
            .append(
                Component.text(
                    e.name.lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                    .color(e.color)
            )
        specificity = 1

        faction = e
    }

    constructor(e: String) {
        when (e) {
            "all" -> PlayableRole.entries.filter { role -> role.roleFaction != PlayableFaction.SPECTATOR }.forEach { role ->
                roles.add(role)
                selectorText = Component.text("Any").color(NamedTextColor.GRAY)
                specificity = -1
            }

            "basic" -> {
                roles.add(PlayableRole.VILLAGER)
                roles.add(PlayableRole.SHADOW)
                roles.add(PlayableRole.SHERIFF)
                selectorText = Component.text("Any ").color(NamedTextColor.GRAY)
                    .append(Component.text("Basic").color(NamedTextColor.BLUE))
                specificity = 0
            }

            "special" -> PlayableRole.entries.forEach { role ->
                if (role == PlayableRole.VILLAGER) return@forEach
                selectorText = Component.text("Any ").color(NamedTextColor.GRAY)
                    .append(Component.text("Special").color(NamedTextColor.BLUE))
                specificity = 0
            }
        }
    }

    fun copyToMutableRoles() {
        mutableRoles.clear()
        mutableRoles.addAll(roles)
    }

    override fun compareTo(other: RolelistSelector): Int {
        if (specificity > other.specificity) return 1
        if (specificity < other.specificity) return -1

        if (faction != null && other.faction != null) {
            if (faction!!.ordinal > other.faction!!.ordinal) return 1
            if (faction!!.ordinal < other.faction!!.ordinal) return -1
        } else if (faction != null) return 1
        else if (other.faction != null) return -1

        if (roles.size > other.roles.size) return 1
        if (roles.size < other.roles.size) return -1

        return (selectorText as TextComponent).content().compareTo((other.selectorText as TextComponent).content())
    }

    override fun toString(): String {
        return (selectorText as TextComponent).content() + "[${specificity}][${roles}]"
    }
}