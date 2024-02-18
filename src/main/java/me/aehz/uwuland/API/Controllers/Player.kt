package me.aehz.uwuland.API.Controllers

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.response.*
import me.aehz.uwuland.API.Data.*
import me.aehz.uwuland.enums.PerkOwnerType
import me.aehz.uwuland.managers.EventManager
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

object PlayerController {
    suspend fun get(call: ApplicationCall) {
        val players = Bukkit.getServer().onlinePlayers.map {
            playerToPlayerData(it)
        }

        val responseData = AllPlayersData(
            players.size,
            players
        )
        call.respond(responseData)
    }

    private fun playerToPlayerData(player: Player): PlayerData {
        return PlayerData(
            player.uniqueId,
            player.name,
            player.level,
            player.health,
            EventManager.getPerksByEntity(player).map {
                PlayerPerkData(
                    it.alias,
                    it.isEnabled,
                    it.perkOwners.filter { it.targets.contains(player.uniqueId) && it.type == PerkOwnerType.PLAYER }
                )
            },
            player.activePotionEffects,
            Attribute.values().mapNotNull { v -> player.getAttribute(v) }.map { attr ->
                PlayerAttributeData(
                    attr.attribute.name,
                    attr.baseValue,
                    attr.value,
                    attr.modifiers,
                )
            }
        )
    }
}