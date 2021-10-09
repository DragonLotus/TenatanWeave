package com.phi.tenatanweave.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
class Deck(
    val id: String = "",
    var deckName: String = "",
    var deckPictureId: String = "",
    var heroId: PrintingWithFinish = PrintingWithFinish(),
    val equipmentList: MutableList<PrintingWithFinish> = mutableListOf(),
    val coreDeckList: MutableList<PrintingWithFinish> = mutableListOf(),
    val sideBoardMap: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val maybeBoardMap: MutableMap<String, MutableList<String>> = mutableMapOf(),
    var format: String = "",
    var lastModifiedDate: String = ""
) {
    private val lastModifiedDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    init {
        if (lastModifiedDate.isEmpty()) {
            setNewLastModifiedDate()
        }
    }

    @Exclude
    fun setNewLastModifiedDate(time: Date = Calendar.getInstance().time) {
        lastModifiedDate = lastModifiedDateFormat.format(time)
    }
}