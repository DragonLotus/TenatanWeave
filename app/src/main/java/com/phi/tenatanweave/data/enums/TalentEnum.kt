package com.phi.tenatanweave.data.enums

enum class TalentEnum {
    ALL,
    SHADOW,
    LIGHT,
    ELEMENTAL,
    EARTH,
    ICE,
    LIGHTNING;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            SHADOW -> "Shadow"
            LIGHT -> "Light"
            ELEMENTAL -> "Elemental"
            EARTH -> "Earth"
            ICE -> "Ice"
            LIGHTNING -> "Lightning"
        }
    }
}