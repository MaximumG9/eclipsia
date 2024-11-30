package dev.osmii.shadow.events.custom

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.RoleModifier
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PacketMakeItemsUndifferentiable(val shadow: Shadow) : PacketAdapter(
    shadow,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.WINDOW_ITEMS
) {
    override fun onPacketSending(e: PacketEvent) {
        if(!shadow.hasRoleModifier(e.player, RoleModifier.GUESS_WHO)) return
        val inventory = e.packet.itemListModifier.read(0)
        for(i in 0..<inventory.size) {
            if(ItemUtil.customIdIs(inventory[i], CID.FAKE_SPECIAL_ITEM) || ItemUtil.customIdIs(inventory[i], CID.TRIDENT) || ItemUtil.customIdIs(inventory[i], CID.INVENTORY_SHERIFF_BOW)) {
                inventory[i] = ItemStack(fakeItems.random())
            }
        }
        val heldSlot = e.packet.itemModifier.read(0)
        if(ItemUtil.customIdIs(heldSlot, CID.FAKE_SPECIAL_ITEM) || ItemUtil.customIdIs(heldSlot, CID.TRIDENT) || ItemUtil.customIdIs(heldSlot, CID.INVENTORY_SHERIFF_BOW)) {
            e.packet.itemModifier.write(0,ItemStack(fakeItems.random()))
        }
        e.packet.itemListModifier.write(0,inventory)
    }

    companion object {
        val fakeItems: Array<Material> = arrayOf(Material.MUSIC_DISC_5,Material.MUSIC_DISC_11,Material.ARROW,Material.BLUE_DYE,Material.RED_DYE,Material.WHITE_DYE,Material.ARROW,Material.BLAZE_ROD,Material.BLAZE_POWDER,Material.CHARCOAL,Material.COAL,Material.RAW_IRON,Material.IRON_INGOT,Material.AMETHYST_SHARD,Material.NETHERITE_SCRAP,Material.CLOCK,Material.CLAY_BALL,Material.DIAMOND,Material.FLINT,Material.EMERALD,Material.COMPASS,Material.BOOK,Material.BONE)
    }
}