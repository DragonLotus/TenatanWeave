package com.phi.tenatanweave.data

import com.google.firebase.database.DataSnapshot
import com.phi.tenatanweave.data.enums.*

class Ruling(
    val id: Long = -1,
    val card: String = "",
    val rulings: MutableList<String> = mutableListOf()
) {
}