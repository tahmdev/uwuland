package me.aehz.uwuland.listener.group_perks

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import org.bukkit.entity.Player

import me.aehz.uwuland.Uwuland
import me.aehz.uwuland.data.PerkOwner
import org.bukkit.Bukkit
import me.aehz.uwuland.managers.EventManager
import me.aehz.uwuland.abstracts.GroupPerkListener
import org.bukkit.EntityEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import kotlin.math.max
import kotlin.math.min


class BindDamage() : GroupPerkListener() {

    var SETTING_damageMultiplier = 1.0

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(e: EntityDamageEvent) {
        if (!isEnabled) return
        if (e.damage == 0.0) return
        if (!hasPerk(e.entity)) return
        val damageTaker = e.entity
        if (damageTaker !is LivingEntity) return
        val dmg = e.finalDamage * SETTING_damageMultiplier
        val partners = getPartners(damageTaker)

        for (partner in partners) {
            partner.playEffect(EntityEffect.HURT)
            partner.health = max(partner.health - dmg, 0.0)

            //Unbind if partner was a mob. DamageTaker damage is not calculated at this point
            if ((partner !is Player && partner.isDead) || (damageTaker !is Player && damageTaker.health <= dmg)) {
                removeHpModifier(damageTaker, partner)
                removeHpModifier(partner, damageTaker)
            }
        }
    }

    @EventHandler
    fun onRespawn(e: PlayerPostRespawnEvent) {
        if (!isEnabled) return
        if (!hasPerk(e.player)) return
        val partners = getPartners(e.player)
        for (partner in partners) {
            Bukkit.getLogger().info("${partner.name} ${partner.health}")
            Bukkit.getLogger().info("${e.player.name} ${e.player.health}")
            if (partner.health > 0) {
                e.player.health = partner.health
            }
        }
    }

    @EventHandler
    fun onReg(e: EntityRegainHealthEvent) {
        if (!isEnabled) return
        if (!hasPerk(e.entity)) return
        val partners = getPartners(e.entity)

        for (partner in partners) {
            if (partner.health <= 0) return
            val partnerMaxHealth = partner.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            partner.health = min(partner.health + e.amount, partnerMaxHealth)
        }
    }

    //TODO SYNC ON JOIN
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (!isEnabled) return
        if (!hasPerk(e.player)) return
        val partners = getPartners(e.player)
        for (partner in partners) {
            addHpModifier(e.player, partner)
            addHpModifier(partner, e.player)
        }
        e.player.health = partners[0].health
    }

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        if (!isEnabled) return
        if (!hasPerk(e.player)) return
        val partners = getPartners(e.player)
        for (partner in partners) {
            removeHpModifier(e.player, partner)
            removeHpModifier(partner, e.player)
        }
    }

    override fun setup(owner: PerkOwner): Boolean {
        val targets = owner.getTargetsAsLivingEntities()
        val totalHp = targets.map { it.health }.reduce { acc, e -> acc + e }
        val totalMaxHp =
            targets.map { it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue }.reduce { acc, e -> acc + e }

        targets.forEach { e1 ->
            targets.forEach { e2 ->
                if (e1 != e2) addHpModifier(e1, e2)
            }
        }

        targets.forEach { it.health = min(totalMaxHp, totalHp) }

        return true
    }

    override fun unsetup(owner: PerkOwner) {
        val targets = owner.getTargetsAsLivingEntities()
        targets.forEach { e1 ->
            targets.forEach { e2 ->
                if (e1 != e2) removeHpModifier(e1, e2)
            }
            e1.health = min(e1.health / targets.size, e1.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue)
        }
    }

    private fun addHpModifier(e1: Entity, e2: Entity) {
        if (e1 is LivingEntity && e2 is LivingEntity) {
            val e2HP = e2.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
            e1.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.addModifier(
                AttributeModifier(
                    e2.uniqueId,
                    "BIND",
                    e2HP,
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
        }
    }

    private fun removeHpModifier(e1: Entity, e2: Entity) {
        if (e1 is LivingEntity) {
            val modifier =
                e1.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.modifiers.filter { it.uniqueId == e2.uniqueId }[0]
            e1.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.removeModifier(modifier)
        }
    }
}
