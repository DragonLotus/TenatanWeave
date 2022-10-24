package com.phi.tenatanweave.data.enums

enum class RarityEnum {
    ALL,
    F,
    L,
    M,
    S,
    MV,
    R,
    C,
    P,
    T;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            F -> "Fabled"
            L -> "Legendary"
            M -> "Majestic"
            S -> "Super Rare"
            MV -> "Marvel"
            R -> "Rare"
            C -> "Common"
            P -> "Promo"
            T -> "Token"
        }
    }
}