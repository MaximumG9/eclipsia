package dev.osmii.shadow.config

import dev.osmii.shadow.enums.Foods
import dev.osmii.shadow.enums.ShadowTestAbilities

class Config {
    var shadowAbility = ShadowTestAbilities.CULL
    var poisonCloudRadius = 10.0
    var poisonCloudDuration = 3 * 60 * 20
    var poisonCloudParticlesPerTick = 20
    var poisonCloudViewDistance = 32.0
    var poisonEffectDuration = 200
    var poisonEffectAmplifier = 2
    var tntExplodeTicks = 30
    var teleportHeight = 30.0
    var cullNightly = true
    var food = Foods.BREAD
    var foodAmount = 16
    var timeToJesterWin = 20 * 60 * 5
}