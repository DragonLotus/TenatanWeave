package com.phi.tenatanweave.data.enums

enum class SubTypeEnum {
    ALL,
    YOUNG,
    CLUB,
    CLAW,
    HAMMER,
    DAGGER,
    SWORD,
    PISTOL,
    GUN,
    BOW,
    STAFF,
    HEAD,
    CHEST,
    ARMS,
    LEGS,
    ARROW,
    TRAP,
    ATTACK,
    ITEM,
    GEM,
    AURA;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            YOUNG -> "Young"
            CLUB -> "Club"
            CLAW -> "Claw"
            HAMMER -> "Hammer"
            DAGGER -> "Dagger"
            SWORD -> "Sword"
            PISTOL -> "Pistol"
            GUN -> "Gun"
            BOW -> "Bow"
            STAFF -> "Staff"
            HEAD -> "Head"
            CHEST -> "Chest"
            ARMS -> "Arms"
            LEGS -> "Legs"
            ARROW -> "Arrow"
            TRAP -> "Trap"
            ATTACK -> "Attack"
            ITEM -> "Item"
            GEM -> "Gem"
            AURA -> "Aura"
        }
    }

}