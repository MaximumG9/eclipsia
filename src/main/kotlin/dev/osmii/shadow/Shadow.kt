package dev.osmii.shadow

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import dev.osmii.shadow.commands.*
import dev.osmii.shadow.enums.PlayableFaction
import dev.osmii.shadow.enums.PlayableRole
import dev.osmii.shadow.enums.RoleModifier
import dev.osmii.shadow.events.HandleItemInteractionRestrict
import dev.osmii.shadow.events.custom.*
import dev.osmii.shadow.events.custom.abilities.HandleAbilityTNTExplosion
import dev.osmii.shadow.events.custom.abilities.item.sheriff.HandleSheriffBow
import dev.osmii.shadow.events.custom.abilities.item.trident.HandleTrident
import dev.osmii.shadow.events.custom.abilities.item.trident.HandleTridentCooldown
import dev.osmii.shadow.events.custom.abilities.menu.HandleAbilities
import dev.osmii.shadow.events.custom.roles.PacketHideItemSwitch
import dev.osmii.shadow.events.custom.roles.PacketKeepGlowing
import dev.osmii.shadow.events.custom.roles.modifiers.PacketMakeItemsUndifferentiable
import dev.osmii.shadow.events.custom.roles.modifiers.PacketMakeItemsUndifferentiableSingle
import dev.osmii.shadow.events.game.*
import dev.osmii.shadow.game.abilities.shadow.CooldownManager
import dev.osmii.shadow.game.abilities.shadow.PoisonCloud
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import org.bukkit.util.BoundingBox
import java.util.*
import java.util.logging.Logger

class Shadow : JavaPlugin() {
    val gameState: ShadowGameState = ShadowGameState()
    private var protocolManager: ProtocolManager? = null
    var strongholdBoundingBox: BoundingBox? = null
    val eyes: HashMap<UUID, Entity> = HashMap()
    val poisonClouds = ArrayList<PoisonCloud>()
    val spawnedTNTs = ArrayList<UUID>()
    val cooldownManager = CooldownManager(this)
    val abilityManager = HandleAbilities(this)

    val overworld: World
        get() = this.server.worlds[0]

    override fun onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager()

        Companion.logger = logger
        Companion.logger!!.info("Enabling Shadow")

        // Register events
        HandleDayNight(this).register()

        Bukkit.getPluginManager().registerEvents(HandleItemInteractionRestrict(), this)
        Bukkit.getPluginManager().registerEvents(HandleChat(this), this)
        Bukkit.getPluginManager().registerEvents(HandleDeath(this), this)
        Bukkit.getPluginManager().registerEvents(HandleJoinLeave(this), this)
        Bukkit.getPluginManager().registerEvents(HandleMoveRestrict(this), this)

        Bukkit.getPluginManager().registerEvents(HandleSheriffBow(this), this)
        Bukkit.getPluginManager().registerEvents(HandleTrident(this), this)
        Bukkit.getPluginManager().registerEvents(HandleTridentCooldown(), this)

        Bukkit.getPluginManager().registerEvents(HandleParticipationToggle(this), this)
        Bukkit.getPluginManager().registerEvents(HandleAddRole(this), this)
        Bukkit.getPluginManager().registerEvents(HandleAddModifier(this), this)
        Bukkit.getPluginManager().registerEvents(abilityManager, this)
        Bukkit.getPluginManager().registerEvents(HandleEyePickup(this), this)

        Bukkit.getPluginManager().registerEvents(HandleAbilityTNTExplosion(this), this)

        protocolManager!!.addPacketListener(PacketHideItemSwitch(this))
        protocolManager!!.addPacketListener(PacketKeepGlowing(this))
        protocolManager!!.addPacketListener(PacketMakeItemsUndifferentiable(this))
        protocolManager!!.addPacketListener(PacketMakeItemsUndifferentiableSingle(this))

        // Register commands
        getCommand("\$roles")!!.setExecutor(CommandRoles(this))
        getCommand("\$location")!!.setExecutor(CommandLocation(this))
        getCommand("\$start")!!.setExecutor(CommandStart(this))
        getCommand("\$cancel")!!.setExecutor(CommandCancel(this))
        getCommand("\$modifiers")!!.setExecutor(CommandRoleModifiers(this))
        getCommand("shadowchat")!!.setExecutor(CommandShadowChat(this))

        CommandConfig(this).register((this.server as CraftServer).server.commands.dispatcher)

        // Create player team
        var team = Bukkit.getScoreboardManager().mainScoreboard.getTeam("players")
        if (team == null) {
            team = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("players")
        }

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)

        val poisonCloudTickerTask = object : BukkitRunnable() {
            override fun run() {
                poisonClouds.forEach(PoisonCloud::tick)
            }
        }
        poisonCloudTickerTask.runTaskTimer(this, 0, 1)
    }

    fun isRoleFaction(player: Player, roleFaction: PlayableFaction) : Boolean {
        return this.gameState.currentRoles.getOrDefault(player.uniqueId,PlayableRole.SPECTATOR).roleFaction == roleFaction
    }
    fun isRole(player: Player, role: PlayableRole) : Boolean {
        return this.gameState.currentRoles.getOrDefault(player.uniqueId,PlayableRole.SPECTATOR) == role
    }
    fun hasRoleModifier(player: Player, role: RoleModifier) : Boolean {
        return this.gameState.currentRoleModifiers[player.uniqueId]?.contains(role) == true
    }

    companion object {
        var logger: Logger? = null
    }
}
