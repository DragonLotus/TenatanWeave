package com.phi.tenatanweave.data

import com.phi.tenatanweave.data.enums.SetEnum

sealed class RecyclerItem {
    data class Printing(val printing: com.phi.tenatanweave.data.Printing) : RecyclerItem()
    data class SetSection(val setName: String) : RecyclerItem()
}