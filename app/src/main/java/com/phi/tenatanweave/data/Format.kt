package com.phi.tenatanweave.data

class Format(
    val id: String = "",
    val name: String = "",
    val maxCopyLimit: Int = -1,
    val deckLimit: Int = -1,
    val deckMinimum: Int = -1,
    val equipmentLimit: Int = -1,
    val includeEquipment: Boolean = false
) {
}