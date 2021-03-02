package com.phi.tenatanweave.data.enums

enum class FormatEnum {
    NONE,
    CONSTRUCTED,
    BLITZ,
    ULTIMATE_PIT_FIGHT;

    override fun toString(): String {
        return when (this) {
            NONE -> "None"
            CONSTRUCTED -> "Classic Constructed"
            BLITZ -> "Blitz"
            ULTIMATE_PIT_FIGHT -> "Ultimate Pit Fight"
        }
    }
}