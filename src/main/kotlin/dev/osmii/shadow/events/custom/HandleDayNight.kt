package dev.osmii.shadow.events.custom

import com.comphenix.protocol.PacketType
import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableFaction
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.network.syncher.SynchedEntityData.DataItem
import net.minecraft.world.entity.Entity
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class HandleDayNight(val shadow: Shadow) {

    val glowingUpdatedFor: ArrayList<Pair<Int, Int>> = ArrayList()

    fun register() {
        val world: World? = shadow.server.worlds[0]
        Bukkit.getScheduler().runTaskTimer(shadow, Runnable {
            if (shadow.gameState.currentPhase != GamePhase.GAME_IN_PROGRESS) return@Runnable

            if (world?.time in 12452L..12532L) {
                glowingUpdatedFor.clear()
                shadow.server.onlinePlayers.forEach { p ->
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.SHADOW) {
                        Audience.audience(p).sendMessage(
                            MiniMessage.miniMessage()
                                .deserialize("<green>Darkness approaches. Your powers grow.</green>")
                        )
                    }
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.VILLAGE ||
                        shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.NEUTRAL
                    ) {
                        Audience.audience(p).sendActionBar(
                            MiniMessage.miniMessage().deserialize("<red>Darkness approaches. It is now nighttime</red>")
                        )
                    }
                }
            }
            if (world?.time in 0L..80L) {
                shadow.server.onlinePlayers.forEach { p ->
                    Audience.audience(p).sendActionBar(
                        MiniMessage.miniMessage().deserialize("<green>The sky clears.</green>")
                    )
                }
            }
            if (world?.time!! >= 12452L) {
                world.time += 9
                shadow.server.onlinePlayers.forEach { p ->
                    if(p.isGlowing) p.isGlowing = false
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.VILLAGE) {
                        p.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false))
                        p.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, 40, 0, false, false))
                    }
                    /*
                    if (shadow.gameState.currentRoles[p.uniqueId]!!.roleFaction == PlayableFaction.SHADOW) {
                        shadow.server.onlinePlayers.forEach inner@{ other ->
                            if (p.entityId == other.entityId) return@inner

                            // If glowing is already updated for this player, don't update it again
                            val alreadyUpdated = glowingUpdatedFor.any { pair ->
                                pair.first == other.entityId && pair.second == p.entityId
                            }
                            if (alreadyUpdated) return@inner

                            shadow.server.broadcast(net.kyori.adventure.text.Component.text(
                                "p: ${p.name}, other: ${other.name}"
                            ))

                            val connection = (p as CraftPlayer).handle.connection

                            val field = Entity::class.java.declaredFields.filter {
                                it.type.name == EntityDataAccessor::class.java.name
                            }.first {
                                (it.genericType as ParameterizedType).actualTypeArguments[0].typeName == java.lang.Byte::class.java.typeName
                            }

                            field.isAccessible = true

                            val accessor : EntityDataAccessor<Byte> = field.get((other as CraftPlayer)) as EntityDataAccessor<Byte>

                            val getItemMethod : Method = SynchedEntityData::class.java.declaredMethods.filter {
                                it.returnType.name == DataItem::class.java.name
                            }.first {
                                it.parameterTypes.size == 1 &&
                                        it.parameterTypes[0].name == EntityDataAccessor::class.java.name
                            }

                            getItemMethod.isAccessible = true

                            val dataItem : DataItem<Byte> = getItemMethod.invoke(other.handle.entityData,accessor) as DataItem<Byte>

                            dataItem.value = (0b01000000.toByte() or dataItem.value)

                            val packet = ClientboundSetEntityDataPacket(other.entityId, listOf(dataItem.value()))


                            try {
                                connection.send(packet)
                            } catch (e: Exception) {
                                shadow.logger.warning("ProtocolLib failure: ${e.message}")
                                e.printStackTrace()
                            }

                            dataItem.value = ((0b01000000.toByte()).inv() and dataItem.value)

                            shadow.logger.info(dataItem.value.toString())

                            glowingUpdatedFor.add(Pair(other.entityId, p.entityId))
                        }
                    } */
                }
            } else {
                shadow.server.onlinePlayers.forEach { p ->
                    if(!p.isGlowing) p.isGlowing = true
                }
            }
        }, 0, 1L)
    }
}