package dev.osmii.shadow.util

import dev.osmii.shadow.Shadow
import java.time.Duration
import kotlin.math.ceil

object TimeUtil {
    fun ticks(ticks: Int): Duration {
        return Duration.ofMillis(ticks.toLong() * 50)
    }

    fun ticksToText(ticks: Int): String {
        return secondsToText(ceil(ticks/20.0).toInt())
    }

    fun secondsToText(seconds: Int): String {
        val secs = seconds % 60
        val minutes = (seconds - secs) / 60 % 60
        val hours = (seconds - secs - minutes * 60) / 3600

        return if (hours > 0) String.format("%d:%02d:%02d", hours, minutes, secs)
        else if (minutes > 0) String.format("%d:%02d", minutes, secs)
        else String.format("0:%02d", secs)
    }

    private val cooldownMap = HashMap<String, HashMap<String, Int>>()

    fun setCooldown(shadow: Shadow, key: String, uuid: String) {
        if (cooldownMap[key] == null) {
            cooldownMap[key] = HashMap()
        }
        cooldownMap[key]!![uuid] = shadow.server.currentTick
    }
}