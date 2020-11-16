package com.phi.tenatanweave.data.enums

enum class SetEnum {
    WTR,
    ARC,
    CRU,
    OXO,
    IRA,
    RNR,
    BVO,
    KSU,
    TEA,
    ARM1,
    POP2019,
    MAP,
    ARCPRP,
    ARCBAP,
    ARM2,
    POP2020,
    PROMO,
    CRUBAP;

    override fun toString(): String {
        return when (this) {
            PROMO -> "Other Promo"
            OXO -> "Slingshot Underground"
            MAP -> "Mighty Ape Promotion"
            IRA -> "Ira Welcome Deck"
            KSU -> "Katsu Starter Deck"
            TEA -> "Dorinthea Starter Deck"
            RNR -> "Rhinar Starter Deck"
            BVO -> "Bravo Starter Deck"
            POP2019 -> "Premier Organized Play 2019"
            POP2020 -> "Premier Organized Play 2020"
            ARM1 -> "Armory Event S1"
            ARM2 -> "Armory Event S2"
            WTR -> "Welcome to Rathe"
            ARCPRP -> "Arcane Rising Pre-Release Promo"
            ARCBAP -> "Arcane Rising Buy-a-Box Promo"
            ARC -> "Arcane Rising"
            CRUBAP -> "Crucible of War Buy-a-Box Promo"
            CRU -> "Crucible of War"
        }
    }
}