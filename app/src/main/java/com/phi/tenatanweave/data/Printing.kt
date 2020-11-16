package com.phi.tenatanweave.data

import com.phi.tenatanweave.data.enums.FinishEnum
import com.phi.tenatanweave.data.enums.RarityEnum
import com.phi.tenatanweave.data.enums.SetEnum

class Printing(
    val id: String = "",
    val name: String = "",
    val version: Int = 0,
    val flavorText: String = "",
    val rarity: RarityEnum = RarityEnum.P,
    val artist: String = "",
    val setCode: String = "",
    var set : ExpansionSet? = null,
    val collectorNumber: Int = 0,
    val finish: MutableList<FinishEnum> = mutableListOf()
) {
}