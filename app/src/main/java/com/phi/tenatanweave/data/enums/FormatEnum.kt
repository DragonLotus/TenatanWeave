package com.phi.tenatanweave.data.enums

enum class FormatEnum {
    CONSTRUCTED,
    BLITZ,
    ULTIMATE_PIT_FIGHT;

    override fun toString(): String {
        return when (this) {
            CONSTRUCTED -> "Classic Constructed"
            BLITZ -> "Blitz"
            ULTIMATE_PIT_FIGHT -> "Ultimate Pit Fight"
        }
    }
}