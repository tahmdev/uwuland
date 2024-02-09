package me.aehz.uwuland

import me.aehz.uwuland.commands.*
import me.aehz.uwuland.listener.*
import me.aehz.uwuland.listener.global_events.*
import me.aehz.uwuland.listener.global_events.Beta
import me.aehz.uwuland.listener.group_perks.BindDamage
import me.aehz.uwuland.listener.group_perks.Shuffle
import me.aehz.uwuland.listener.perks.*
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object PluginInstance {
    var instance: Uwuland? = null
    fun get(): Uwuland? {
        return instance
    }

    fun set(plugin: Uwuland) {
        instance = plugin
    }
}

class Uwuland : JavaPlugin() {


    override fun onEnable() {

        PluginInstance.set(this)

        File("./worlds.txt").forEachLine { name -> WorldCreator(name).createWorld() }
        //Event listeners
        ProtectDBBlock(this)


        //PEKRS (manage perks inside EventManager)
        NarutoSwap()
        Tesla()
        Firestarter()
        Xray()
        Speedrunner()
        Naruto()
        Abductor()
        Disorganized()

        // TODO ADD Speedrunner infinite glass, slowly refilling
        // TODO MAKE RandomFallDamage a solo perk. Also make it able to heal

        // Photosynthesis: Gain buffs / debuffs based on light level (reapply every second )
        // Magnet: Attract everything (less range / effect on players)

        // Bunny: Takes damage when alone. Implement BunnyJump. (No fall damage?) DONT LET ENDERDRAGON BE BUNNIFIED
        // ShortSighted: PLAYER ONLY Unable to see Entities outside a 4 block radius. Unable to be seen by other Entities outside a 4 block radius
        SwapStick()
        BunnyJump()

        //GROUP EVENTS
        Shuffle()
        BindDamage()

        //GLOBAL EVENTS
        ExplosiveArrows()
        RandomFallDamage()
        PlayerJoinGreeting()
        Beta()

        //Commands
        me.aehz.uwuland.commands.Beta(this)
        WorldTeleport(this)
        EventToggle(this)
        Settings(this)
        Perk(this)
        GroupPerk(this)
        //ToggleShuffle(this) // REWORK THIS TO BE TIMED
        //InitializeDBWorld(this)
        //CreateWorld(this)

        // GROUP EVENTS: /group_event <[Online_players]|[teams]> <add|remove> <event>

        // TIMED EVENTS: Always gets executed after X time.
        // Shuffle example: Store array of [array of players] and shuffle each array


        //IDEAS:
        // Camera plugin. Register spectator =>
    }

    override fun onDisable() {
    }


}
