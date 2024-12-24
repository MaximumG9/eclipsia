package dev.osmii.shadow.events.custom.roles

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class PacketKeepGlowing(val shadow: Shadow) : PacketAdapter(
    shadow,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.ENTITY_METADATA
) {
    override fun onPacketSending(e: PacketEvent) {
        try {
            val other = shadow.server.onlinePlayers.firstOrNull { it.entityId == e.packet.integers.read(0) }
            if (other == null) return

            // if (other.uniqueId == e.player.uniqueId) return

            val p = e.packet.deepClone()

            val nmsPacket : ClientboundSetEntityDataPacket = p.handle as ClientboundSetEntityDataPacket

            if(shadow.isRoleFaction(e.player, PlayableFaction.SHADOW) || shadow.overworld.time < 12452L) {
                nmsPacket.packedItems.forEachIndexed { i, data ->
                    if(data.id == 0) {
                        nmsPacket.packedItems[i] = SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE,(data.value as Byte) or ((1 shl 6).toByte()))
                    }
                }
            } else {
                nmsPacket.packedItems.forEachIndexed { i, data ->
                    if(data.id == 0) {
                        nmsPacket.packedItems[i] = SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE,(data.value as Byte) and ((1 shl 6).toByte().inv()))
                    }
                }
            }

            e.packet = p
        } catch (e : Throwable) {
            shadow.server.broadcast(
                Component.text(e.stackTraceToString())
            )
        }
    }
}
