package dev.osmii.shadow.events.custom

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.CID
import dev.osmii.shadow.enums.RoleModifier
import dev.osmii.shadow.events.custom.PacketMakeItemsUndifferentiable.Companion.fakeItems
import dev.osmii.shadow.util.ItemUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PacketMakeItemsUndifferentiableSingle(val shadow: Shadow) : PacketAdapter(
    shadow,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.SET_SLOT
) {
    override fun onPacketSending(e: PacketEvent) {
        if(!shadow.hasRoleModifier(e.player, RoleModifier.GUESS_WHO)) return

        val modifiedSlot = e.packet.itemModifier.read(0)
        if(ItemUtil.customIdIs(modifiedSlot, CID.FAKE_SPECIAL_ITEM) || ItemUtil.customIdIs(modifiedSlot, CID.TRIDENT) || ItemUtil.customIdIs(modifiedSlot, CID.INVENTORY_SHERIFF_BOW)) {
            e.packet.itemModifier.write(0,ItemStack(fakeItems.random()))
        }
    }
}