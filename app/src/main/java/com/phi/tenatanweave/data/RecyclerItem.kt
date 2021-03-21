package com.phi.tenatanweave.data

sealed class RecyclerItem {
    data class Printing(val printing: com.phi.tenatanweave.data.Printing) : RecyclerItem()
    data class SetSection(var setName: String) : RecyclerItem()
    data class HeroPrinting(val cardPrinting: com.phi.tenatanweave.data.CardPrinting?) : RecyclerItem()
    data class CardPrinting(val cardPrinting: com.phi.tenatanweave.data.CardPrinting) : RecyclerItem()
}