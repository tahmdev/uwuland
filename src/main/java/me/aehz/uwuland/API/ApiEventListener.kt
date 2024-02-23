package me.aehz.uwuland.API

import me.aehz.uwuland.API.Data.*
import me.aehz.uwuland.PluginInstance
import me.aehz.uwuland.enums.ApiEventType
import me.aehz.uwuland.managers.ApiEventManager
import org.bukkit.Bukkit
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerQuitEvent

class ApiEventListener : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, PluginInstance.get()!!)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(e: EntityDamageEvent) {
        val isTamed = e.entity is Tameable && (e.entity as Tameable).isTamed
        if (e.entity !is Player && !isTamed) return

        val data = ApiDataEvent.Damage(
            ApiEventType.DAMAGE,
            ApiDataConverter.entity(e.entity),
            e.finalDamage,
            e.cause.name
        )
        ApiEventManager.add(data)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if (e.entity !is Player || e.damager !is Player) return
        val data = ApiDataEvent.Pvp(
            ApiEventType.DAMAGE,

            ApiDataConverter.entity(e.entity),
            ApiDataConverter.entity(e.damager),
            e.finalDamage,
            e.cause.name,
        )
        ApiEventManager.add(data)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDeath(e: EntityDeathEvent) {
        val isTamed = e.entity is Tameable && (e.entity as Tameable).isTamed
        if (e.entity !is Player && !isTamed) return
        val data = ApiDataEvent.Death(
            ApiEventType.DAMAGE,

            ApiDataConverter.entity(e.entity),
        )
        ApiEventManager.add(data)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(e: PlayerJoinEvent) {
        val data = ApiDataEvent.JoinQuit(
            ApiEventType.DAMAGE,
            ApiDataConverter.entity(e.player),
            e.player.ping
        )
        ApiEventManager.add(data)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onQuit(e: PlayerQuitEvent) {
        val data = ApiDataEvent.JoinQuit(
            ApiEventType.DAMAGE,
            ApiDataConverter.entity(e.player),
            e.player.ping
        )
        ApiEventManager.add(data)
    }

    @EventHandler
    fun onPortal(e: PlayerPortalEvent) {
        val data = ApiDataEvent.Portal(
            ApiEventType.PORTAL,
            ApiDataConverter.entity(e.player),
            ApiDataConverter.location(e.from),
            ApiDataConverter.location(e.to)
        )
        ApiEventManager.add(data)
    }
}