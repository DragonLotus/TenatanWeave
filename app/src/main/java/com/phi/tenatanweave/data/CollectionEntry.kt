package com.phi.tenatanweave.data

class CollectionEntry(
    val collectorNumber : Int = 0,
    val collectorId : String = "",
    val setCode : String = "",
    val quantityList : MutableList<Int> = mutableListOf()
) {
}