package dev.osmii.shadow.events.custom

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.ScheduledPacket
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import net.kyori.adventure.text.Component
import kotlin.experimental.or

class PacketKeepGlowing(val shadow: Shadow) : PacketAdapter(
    shadow,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.ENTITY_METADATA
) {
    override fun onPacketSending(e: PacketEvent) {
        try {
            if (e.packetType != PacketType.Play.Server.ENTITY_METADATA) return
            if (shadow.overworld.time <= 12452L) return
            if (!shadow.isRoleFaction(e.player,PlayableFaction.SHADOW)) return

            if (!shadow.server.onlinePlayers.any { it.entityId == e.packet.integers.read(0) }) return

            val pOrg = e.packet

            val p = pOrg.deepClone()

            e.isCancelled = true

            p.dataValueCollectionModifier.values.forEach { wrappedDataValues ->
                wrappedDataValues.filter {
                    it.index == 0
                }.forEach {
                    it.value = 0b01000000.toByte() or (it.value as Byte)
                }
            }

            e.schedule(ScheduledPacket.fromSilent(p, e.player))
        } catch (e : Throwable) {
            shadow.server.broadcast(
                Component.text(e.stackTraceToString())
            )
        }
    }
}