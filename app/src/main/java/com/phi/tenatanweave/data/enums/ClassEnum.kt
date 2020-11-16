package com.phi.tenatanweave.data.enums

enum class ClassEnum {
    ALL,
    BRUTE,
    GUARDIAN,
    NINJA,
    WARRIOR,
    MECHANOLOGIST,
    RUNEBLADE,
    RANGER,
    WIZARD,
    SHAPESHIFTER,
    MERCHANT,
    GENERIC;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            BRUTE -> "Brute"
            GUARDIAN -> "Guardian"
            NINJA -> "Ninja"
            WARRIOR -> "Warrior"
            MECHANOLOGIST -> "Mechanologist"
            RUNEBLADE -> "Runeblade"
            RANGER -> "Ranger"
            WIZARD -> "Wizard"
            SHAPESHIFTER -> "Shapeshifter"
            MERCHANT -> "Merchant"
            GENERIC -> "Generic"
        }
    }
}