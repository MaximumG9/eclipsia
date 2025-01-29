package dev.osmii.shadow.game.abilities.shadow

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.PlayableFaction
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class PoisonCloud(val shadow : Shadow, val location: Location) {
    private var ticksLeft = shadow.config.poisonCloudDuration
    fun tick() {
        if(ticksLeft <= 0) {
            shadow.poisonClouds.remove(this)
        }
        shadow.server.onlinePlayers.stream().forEach {
            if(it.world == location.world) {

                // Give Poison Effect
                if(it.location.distanceSquared(location) < shadow.config.poisonCloudRadius*shadow.config.poisonCloudRadius) {
                    val potentionalPoisonEffect =  it.getPotionEffect(PotionEffectType.POISON)

                    if( !(potentionalPoisonEffect != null &&
                        potentionalPoisonEffect.duration + 20 > shadow.config.poisonEffectDuration &&
                        potentionalPoisonEffect.amplifier >= shadow.config.poisonEffectAmplifier
                                )) {
                        it.addPotionEffect(
                            PotionEffect(
                                PotionEffectType.POISON,
                                shadow.config.poisonEffectDuration,
                                shadow.config.poisonEffectAmplifier,
                                false,
                                true,
                                true
                            )
                        )
                    }

                    if(shadow.gameState.currentRoles[it.uniqueId]?.roleFaction == PlayableFaction.SHADOW) { // If is a shadow

                        val potentionalRegenEffect = it.getPotionEffect(PotionEffectType.REGENERATION)

                        if( potentionalRegenEffect == null ||
                            (potentionalRegenEffect.duration + 20 < shadow.config.poisonEffectDuration &&
                            potentionalRegenEffect.amplifier <= shadow.config.poisonEffectAmplifier + 1)
                        ) {
                            it.addPotionEffect(
                                PotionEffect(
                                    PotionEffectType.REGENERATION,
                                    shadow.config.poisonEffectDuration,
                                    shadow.config.poisonEffectAmplifier + 1,
                                    false,
                                    true,
                                    true
                                )
                            )
                        }
                    }

                }

                // Spawn Particles
                if(it.location.distanceSquared(location) < shadow.config.poisonCloudViewDistance * shadow.config.poisonCloudViewDistance) {
                    val particle: Particle = Particle.SPELL_MOB

                    for(i in 0..shadow.config.poisonCloudParticlesPerTick) {

                        val pitch = Random.nextDouble() * TAU
                        val yaw = Random.nextDouble() * TAU

                        val magnitude = adjust(Random.nextDouble()) * shadow.config.poisonCloudRadius

                        val x = cos(pitch) * sin(yaw) * magnitude
                        val z = cos(yaw) * cos(pitch) * magnitude
                        val y = sin(pitch) * magnitude

                        val particleLoc = Location(it.world,x,y,z).add(location)

                        it.spawnParticle(particle,particleLoc,1)
                    }
                }
            }
        }
        ticksLeft--
    }

    companion object {

        const val TAU = Math.PI*2

        fun adjust(x : Double) : Double {
            return sqrt(x)
        }
    }
}