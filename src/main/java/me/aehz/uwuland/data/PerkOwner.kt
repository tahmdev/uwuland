package me.aehz.uwuland.data

import me.aehz.uwuland.enums.PerkOwnerType
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.UUID

data class PerkOwner(
    val type: PerkOwnerType,
    val groupAlias: String,
    val targets: MutableList<UUID> = mutableListOf(),
    val combinedUniqueIdString: String = ""
) {
    var taskId: Int = -1
    private var lastUsed: Long = System.currentTimeMillis() / 1000

    fun getTargetsAsLivingEntities(): MutableList<LivingEntity> {
        if (type == PerkOwnerType.TEAM) {
            val teamName = groupAlias.substringAfter(":")
            return Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)?.entries?.mapNotNull {
                Bukkit.getPlayer(it)
            }?.toMutableList() ?: mutableListOf()
        }
        return targets.mapNotNull { Bukkit.getEntity(it) }.filterIsInstance<LivingEntity>().toMutableList()
    }

    fun isOnCooldown(cooldown: Int): Boolean {
        val now = System.currentTimeMillis() / 1000
        val result = now - lastUsed < cooldown
        Bukkit.getLogger().info("${now - lastUsed} < $cooldown : $result")
        return result
    }

    fun updateCooldown(cooldown: Int) {
        lastUsed = System.currentTimeMillis() / 1000
    }
}
