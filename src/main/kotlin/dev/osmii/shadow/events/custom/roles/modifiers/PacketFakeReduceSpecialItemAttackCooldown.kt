package dev.osmii.shadow.events.custom.roles.modifiers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import dev.osmii.shadow.Shadow

class PacketFakeReduceSpecialItemAttackCooldown(val shadow: Shadow) : PacketAdapter(
    shadow,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.UPDATE_ATTRIBUTES,
    PacketType.Play.Server.UPDATE_ATTRIBUTES,
) {
    override fun onPacketSending(e: PacketEvent) {
        /*
        if(
            !ItemUtil.customIdIs(e.player.inventory.itemInMainHand, CID.FAKE_SPECIAL_ITEM) &&
            !ItemUtil.customIdIs(e.player.inventory.itemInMainHand, CID.INVENTORY_SHERIFF_BOW) &&
            !ItemUtil.customIdIs(e.player.inventory.itemInMainHand, CID.TRIDENT)
        ) return

        shadow.logger.info("relavent attributes updated")

        val attributes = e.packet.attributeCollectionModifier.read(0)

        attributes.map { attribute ->
            shadow.logger.info(attribute.base?.key)
            if(attribute.base?.key == "generic.attack_speed") {
                attribute.base?.defaultValue = 4.0
                attribute.withModifiers(emptySet())
            }
        }
        e.packet.attributeCollectionModifier.write(0, attributes)
        */
    }
}