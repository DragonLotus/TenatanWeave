package com.phi.tenatanweave.data.enums

enum class FinishEnum {
    ALL,
    REGULAR,
    RAINBOW,
    COLD,
    GOLD;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            REGULAR -> "Regular"
            RAINBOW -> "Rainbow"
            COLD -> "Cold"
            GOLD -> "Gold"
        }
    }
}