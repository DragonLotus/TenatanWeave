package com.phi.tenatanweave.data.enums

enum class RarityEnum {
    ALL,
    F,
    L,
    M,
    S,
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
            R -> "Rare"
            C -> "Common"
            P -> "Promo"
            T -> "Token"
        }
    }
}