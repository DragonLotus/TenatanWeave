package com.phi.tenatanweave.data

sealed class AdapterUpdate {
    data class Remove(val index : Int) : AdapterUpdate()
    data class Changed(val index : Int) : AdapterUpdate()
}