package com.phi.tenatanweave.data

import androidx.annotation.Nullable
import com.phi.tenatanweave.data.enums.SetEnum

sealed class RecyclerItem {
    data class Printing(val printing: com.phi.tenatanweave.data.Printing) : RecyclerItem()
    data class SetSection(val setName: String) : RecyclerItem()
    data class HeroPrinting(val printing: com.phi.tenatanweave.data.Printing?) : RecyclerItem()
    data class CardPrinting(val cardPrinting: com.phi.tenatanweave.data.CardPrinting) : RecyclerItem()
}