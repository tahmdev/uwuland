package me.aehz.uwuland.listener.perks

import me.aehz.uwuland.Uwuland
import me.aehz.uwuland.data.PerkOwner
import me.aehz.uwuland.abstracts.PerkListener
import me.aehz.uwuland.managers.EventManager
import me.aehz.uwuland.util.BlockUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent


class Firestarter(
    override val plugin: Uwuland,
) : PerkListener() {

    init {
        stg["min"] = "420"
        stg["max"] = "720"
        stg["amount"] = "4"
        stg["range"] = "12"
        Bukkit.getPluginManager().registerEvents(this, plugin)
        EventManager.register(this, type)
    }

    override fun setup(owner: PerkOwner): Boolean {
        startTask(owner)
        return true
    }

    override fun task(targets: MutableList<LivingEntity>) {
        val perkEntity = targets[0]
        val range = stg["range"]!!.toDouble()
        val burnableBlocks = BlockUtil.getRadiusSolid(perkEntity.location.block, range).filter { it.isBurnable }

        if (burnableBlocks.isEmpty()) return

        val burnableAir =
            burnableBlocks.mapNotNull { block ->
                BlockUtil.getAdjacent(block)
                    .filter { it.type == Material.AIR && it.getRelative(BlockFace.DOWN).isSolid }
                    .randomOrNull()
            }.toMutableList()

        var i = 0
        while (i < stg["amount"]!!.toInt() && burnableAir.isNotEmpty()) {
            val block = burnableAir.random()
            block.type = Material.FIRE
            burnableAir.removeIf { it == block }
            i += 1
        }
    }


    @EventHandler
    fun onDmg(e: EntityDamageEvent) {
        if (!isEnabled) return
        if (!hasPerk(e.entity)) return
        if (e.cause == EntityDamageEvent.DamageCause.FIRE || e.cause == EntityDamageEvent.DamageCause.FIRE_TICK || e.cause == EntityDamageEvent.DamageCause.LAVA) {
            e.isCancelled = true
        }
    }
}