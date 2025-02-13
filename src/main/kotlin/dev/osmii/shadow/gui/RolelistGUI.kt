package dev.osmii.shadow.gui

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.*
import dev.osmii.shadow.util.ItemUtil
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class RolelistGUI(private val shadow: Shadow) {

    fun showGuessRoleInventory(player: Player, roleFilter: (PlayableRole) -> Boolean, callback : (player: Player, role: PlayableRole) -> Unit) {
        val inv = Bukkit.createInventory(null, 54, Component.text("Guess Role").color(NamedTextColor.DARK_RED))

        PlayableRole.entries.filter(roleFilter).forEach { role ->
            if (role == PlayableRole.SPECTATOR) return@forEach
            val item = ItemStack(role.roleIcon)
            item.itemMeta = item.itemMeta.apply {
                displayName(Component.text(role.roleName).color(role.roleColor))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_GUESS_ROLE)
                persistentDataContainer.set(
                    Namespace.ROLE_SELECT_GUESS_ROLE,
                    PersistentDataType.STRING,
                    role.name
                )
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
            inv.addItem(item)
        }

        player.openInventory(inv)

        shadow.guiCallbacks[inv] = { _, inventory, item ->
            if (ItemUtil.customIdIs(item, CID.ROLE_SELECT_GUESS_ROLE)) {
                val roleName = item.itemMeta.persistentDataContainer[Namespace.ROLE_SELECT_GUESS_ROLE,PersistentDataType.STRING]
                val role = PlayableRole.valueOf(roleName!!)
                callback.invoke(player,role)
            }
        }
    }

    fun showAddRoleInventory(player: Player) {
        val inv = Bukkit.createInventory(null, 54, Component.text("Add Role").color(NamedTextColor.BLUE))

        PlayableRole.entries.forEach { role ->
            if (role == PlayableRole.SPECTATOR) return@forEach
            val item = ItemStack(role.roleIcon)
            item.itemMeta = item.itemMeta.apply {
                displayName(Component.text(role.roleName).color(role.roleColor))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_ADD_ROLE)
                persistentDataContainer.set(
                    Namespace.ROLE_SELECT_ADD_ROLE,
                    PersistentDataType.STRING,
                    "role-${role.name}"
                )
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
            inv.addItem(item)
        }

        PlayableSubfaction.entries.forEach { role ->
            if (role == PlayableSubfaction.SPECTATOR) return@forEach
            val item = ItemStack(role.factionIcon)

            val lower = role.name.lowercase(Locale.getDefault()).replace("_", " ")
            val word1 = lower.split(" ").first()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val word2 = lower.split(" ").last()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val name = "$word1 $word2"
            item.itemMeta = item.itemMeta.apply {
                displayName(Component.text(name).color(role.color).decoration(TextDecoration.ITALIC, false))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_ADD_ROLE)
                persistentDataContainer.set(
                    Namespace.ROLE_SELECT_ADD_ROLE,
                    PersistentDataType.STRING,
                    "subfaction-${role.name}"
                )
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
            inv.addItem(item)
        }

        PlayableFaction.entries.forEach { role ->
            if (role == PlayableFaction.SPECTATOR) return@forEach
            val item = ItemStack(role.icon)

            val lower = role.name.lowercase(Locale.getDefault()).replace("_", " ")
            val name = lower
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            item.itemMeta = item.itemMeta.apply {
                displayName(
                    Component.text("Random ").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(name).color(role.color).decoration(TextDecoration.ITALIC, false))
                )
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_ADD_ROLE)
                persistentDataContainer.set(
                    Namespace.ROLE_SELECT_ADD_ROLE,
                    PersistentDataType.STRING,
                    "faction-${role.name}"
                )
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
            inv.addItem(item)
        }

        ItemStack(Material.NETHER_STAR).apply {
            itemMeta = itemMeta.apply {
                displayName(MiniMessage.miniMessage().deserialize("<!i><blue>Any</blue> <gray>Special</gray></!i>"))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_ADD_ROLE)
                persistentDataContainer.set(Namespace.ROLE_SELECT_ADD_ROLE, PersistentDataType.STRING, "special")
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
        }.let { inv.addItem(it) }

        ItemStack(Material.NETHER_STAR).apply {
            itemMeta = itemMeta.apply {
                displayName(MiniMessage.miniMessage().deserialize("<!i><blue>Any</blue> <gray>Basic</gray></!i>"))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_ADD_ROLE)
                persistentDataContainer.set(Namespace.ROLE_SELECT_ADD_ROLE, PersistentDataType.STRING, "basic")
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
        }.let { inv.addItem(it) }

        ItemStack(Material.NETHER_STAR).apply {
            itemMeta = itemMeta.apply {
                displayName(MiniMessage.miniMessage().deserialize("<!i><gray>Any</gray></!i>"))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.ROLE_SELECT_ADD_ROLE)
                persistentDataContainer.set(Namespace.ROLE_SELECT_ADD_ROLE, PersistentDataType.STRING, "all")
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
        }.let { inv.addItem(it) }

        player.openInventory(inv)
    }

    fun showRoleBook(player: Player) {
        var book = Book.book(
            Component.text("Roles").color(NamedTextColor.BLUE),
            Component.text("Shadow").color(NamedTextColor.GRAY),
        )

        // Split into pages of 14 roles
        val pages = shadow.gameState.originalRolelist.getSelectors().chunked(14)
        val pageList = ArrayList<Component>()
        pages.forEachIndexed { pageNumber, page ->
            val pageComponent = Component.join(
                JoinConfiguration.separator(Component.newline()),
                page.map { role ->
                    // Add text to remove a role from the list
                    role.selectorText?.hoverEvent(
                        Component.text("Click to remove this role from the role list").color(NamedTextColor.GRAY)
                    )
                        ?.clickEvent(
                            ClickEvent.clickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/\$roles remove $pageNumber ${page.indexOf(role)}"
                            )
                        )
                }
            )
            pageList.add(pageComponent)
        }

        // Controls to add new roles and clear the list
        pageList.add(
            MiniMessage.miniMessage().deserialize("<dark_green>[+] Add role</dark_green>")
                .hoverEvent(Component.text("Click to add a role").color(NamedTextColor.GRAY))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/\$roles add"))
                .append(Component.newline())
                .append(
                    MiniMessage.miniMessage().deserialize("<red>[-] Clear All</red>")
                        .hoverEvent(Component.text("Click to clear the role list").color(NamedTextColor.GRAY))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/\$roles removeall"))
                )
        )

        book = book.pages(pageList)

        player.openBook(book)
    }
}