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
    AFFLICTION,
    AURA,
    DEMON,
    DRAGON,
    INVOCATION,
    ASH,
    ALLY,
    LANDMARK,
    SCEPTER,
    ORB,
    AXE,
    FLAIL,
    SCYTHE,
    OFF_HAND,
    ROCK;

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
            DEMON -> "Demon"
            ALLY -> "Ally"
            LANDMARK -> "Landmark"
            SCEPTER -> "Scepter"
            ORB -> "Orb"
            AXE -> "Axe"
            FLAIL -> "Flail"
            SCYTHE -> "Scythe"
            OFF_HAND -> "Off-Hand"
            DRAGON -> "Dragon"
            ASH -> "Ash"
            INVOCATION -> "Invocation"
            AFFLICTION -> "Affliction"
            ROCK -> "Rock"
        }
    }

}