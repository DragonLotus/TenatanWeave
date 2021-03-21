package com.phi.tenatanweave.data

import java.text.SimpleDateFormat
import java.util.*

class Deck(
    val id: String = "",
    var deckName: String = "",
    var deckPictureId: String = "",
    var heroId: PrintingWithFinish = PrintingWithFinish(),
    val equipmentList: MutableList<PrintingWithFinish> = mutableListOf(),
    val coreDeckList: MutableList<PrintingWithFinish> = mutableListOf(),
    val sideBoardMap: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val maybeBoardMap: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val foilMap: MutableMap<String, Int> = mutableMapOf(),
    var format: String = "",
    var lastModifiedDate: String = ""
) {
    private val lastModifiedDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    init {
        if (lastModifiedDate.isEmpty()) {
            setNewLastModifiedDate()
        }
    }

    fun setNewLastModifiedDate(time: Date = Calendar.getInstance().time) {
        lastModifiedDate = lastModifiedDateFormat.format(time)
    }
}