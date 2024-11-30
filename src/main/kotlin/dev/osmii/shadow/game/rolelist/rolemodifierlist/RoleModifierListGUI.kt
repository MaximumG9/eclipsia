package dev.osmii.shadow.game.rolelist.rolemodifierlist

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.*
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class RoleModifierListGUI(private val shadow: Shadow) {
    fun showAddRoleModifierInventory(player: Player) {
        val inv = Bukkit.createInventory(null, 54, Component.text("Add Role Modifier").color(NamedTextColor.BLUE))

        RoleModifier.entries.forEach { modifier ->
            val item = ItemStack(modifier.icon)
            item.itemMeta = item.itemMeta.apply {
                displayName(Component.text(modifier.modifierName).color(modifier.color))
                persistentDataContainer.set(Namespace.CUSTOM_ID, PersistentDataType.STRING, CID.MODIFIER_SELECT_ADD_MODIFIER)
                persistentDataContainer.set(
                    Namespace.MODIFIER_SELECT_ADD_MODIFIER,
                    PersistentDataType.STRING,
                    modifier.name
                )
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            }
            inv.addItem(item)
        }

        player.openInventory(inv)
    }

    fun showBook(player: Player) {
        var book = Book.book(
            Component.text("Roles").color(NamedTextColor.BLUE),
            Component.text("Shadow").color(NamedTextColor.GRAY),
        )

        // Split into pages of 14 roles
        val modifiers = shadow.gameState.roleModifierList.getModifiers()
        val pages = modifiers.chunked(5)
        val pageList = ArrayList<Component>()
        pages.forEachIndexed { pageNumber, page ->
            val pageComponent = Component.join(
                JoinConfiguration.separator(Component.newline()),
                page.map { selector ->
                    val modifier = selector.modifier
                    val chance = selector.chance
                    val count = selector.count
                    // Add text to remove a role from the list
                    Component.text("")
                        .append(
                            Component.text("[+] ")
                                .color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/\$modifiers inc $pageNumber ${page.indexOf(selector)}"
                                )
                        )
                        ).append(
                            Component.text("$count ${modifier.modifierName}").clickEvent(null).color(modifier.color)
                        ).append(
                            Component.text(" [-]").color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/\$modifiers dec $pageNumber ${page.indexOf(selector)}"
                                ))
                        ).appendNewline().append(
                            Component.text("[++] ").color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/\$modifiers incperc $pageNumber ${page.indexOf(selector)} 10"
                                ))
                        ).append(
                            Component.text("[+] ").color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/\$modifiers incperc $pageNumber ${page.indexOf(selector)} 1"
                                ))
                        ).append(
                            Component.text("$chance% ").color(NamedTextColor.BLUE)
                        ).append(
                            Component.text("[-] ").color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/\$modifiers decperc $pageNumber ${page.indexOf(selector)} 1"
                                ))
                        ).append(
                            Component.text("[--] ").color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/\$modifiers decperc $pageNumber ${page.indexOf(selector)} 10"
                                ))
                        )

                }
            )
            pageList.add(pageComponent)
        }

        // Controls to add new roles and clear the list
        pageList.add(
            MiniMessage.miniMessage().deserialize("<dark_green>[+] Add Role Modifier</dark_green>")
                .hoverEvent(Component.text("Click to add a role modifier").color(NamedTextColor.GRAY))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/\$modifiers add"))
        )

        book = book.pages(pageList)

        player.openBook(book)
    }
}