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
                inventory[i] = fakeItem
            }
        }
        val heldSlot = e.packet.itemModifier.read(0)
        if(ItemUtil.customIdIs(heldSlot, CID.FAKE_SPECIAL_ITEM) || ItemUtil.customIdIs(heldSlot, CID.TRIDENT) || ItemUtil.customIdIs(heldSlot, CID.INVENTORY_SHERIFF_BOW)) {
            e.packet.itemModifier.write(0,fakeItem)
        }
        e.packet.itemListModifier.write(0,inventory)
    }

    companion object {
        val fakeItem = ItemStack(Material.BLAZE_ROD)
    }
}