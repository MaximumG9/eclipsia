package dev.osmii.shadow.events.game

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.Namespace
import org.bukkit.persistence.PersistentDataType

class PacketHideItemSwitch(val shadow: Shadow) : PacketAdapter(
    shadow,
    ListenerPriority.NORMAL,
    PacketType.Play.Server.ENTITY_EQUIPMENT
) {
    override fun onPacketSending(e: PacketEvent) {
        if (e.packetType != PacketType.Play.Server.ENTITY_EQUIPMENT) return
        if (e.player.entityId == e.packet.integers.read(0)) return
        val p = e.packet

        val equipment = p.slotStackPairLists.read(0)
        val itemSlot = equipment[0].first
        val item = equipment[0].second

        if (itemSlot != EnumWrappers.ItemSlot.MAINHAND) return

        item.itemMeta?.persistentDataContainer?.get(Namespace.INVISIBLE, PersistentDataType.BOOLEAN)?.let {
            if (it) {
                e.isCancelled = true
            }
        }
    }
}