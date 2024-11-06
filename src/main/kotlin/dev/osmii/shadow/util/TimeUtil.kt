package dev.osmii.shadow.util

import dev.osmii.shadow.Shadow
import org.bukkit.Bukkit
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

    fun checkCooldown(shadow: Shadow, seconds: Int, initial: Int, key: String, uuid: String): Int {
        if (cooldownMap[key] == null) {
            cooldownMap[key] = HashMap()
        }
        if (cooldownMap[key]!![uuid] == null) {
            cooldownMap[key]!![uuid] = -1
        }

        val gameStart = shadow.gameState.startTick
        if (shadow.server.currentTick - gameStart < initial * 20) {
            return (initial * 20 - (shadow.server.currentTick - gameStart)) / 20
        }
        if (Bukkit.getServer().currentTick - cooldownMap[key]!![uuid]!! < seconds * 20 && cooldownMap[key]!![uuid]!! != -1) {
            return (seconds * 20 - (shadow.server.currentTick - cooldownMap[key]!![uuid]!!)) / 20
        }

        return 0
    }

    fun setCooldown(shadow: Shadow, key: String, uuid: String) {
        if (cooldownMap[key] == null) {
            cooldownMap[key] = HashMap()
        }
        cooldownMap[key]!![uuid] = shadow.server.currentTick
    }
}