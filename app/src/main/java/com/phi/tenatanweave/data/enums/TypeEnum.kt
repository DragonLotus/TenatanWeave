package com.phi.tenatanweave.data.enums

enum class TypeEnum {
    ALL,
    HERO,
    EQUIPMENT,
    WEAPON,
    ACTION,
    ATTACK_REACTION,
    DEFENSE_REACTION,
    RESOURCE,
    INSTANT,
    TOKEN;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            HERO -> "Hero"
            EQUIPMENT -> "Equipment"
            WEAPON -> "Weapon"
            ACTION -> "Action"
            ATTACK_REACTION -> "Atk. Reaction"
            DEFENSE_REACTION -> "Def. Reaction"
            RESOURCE -> "Resource"
            INSTANT -> "Instant"
            TOKEN -> "Token"
        }
    }
    fun toFullString(): String {
        return when (this) {
            ATTACK_REACTION -> "Attack Reaction"
            DEFENSE_REACTION -> "Defense Reaction"
            else -> this.toString()
        }
    }
}