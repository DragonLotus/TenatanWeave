package com.phi.tenatanweave.data.enums

enum class CompareTypeEnum {
    COST,
    POWER,
    DEFENSE;

    override fun toString(): String {
        return when (this) {
            COST -> "Cost"
            POWER -> "Power"
            DEFENSE -> "Defense"
        }
    }
}