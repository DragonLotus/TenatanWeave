package com.phi.tenatanweave.data

import com.phi.tenatanweave.data.enums.FormatEnum
import java.text.SimpleDateFormat
import java.util.*

class Deck(
    val id: String = "",
    val deckName: String = "",
    val deckPictureId: String = "",
    val heroId: String = "",
    val equipmentList: MutableList<PrintingWithFinish> = mutableListOf(),
    val coreDeckList: MutableList<PrintingWithFinish> = mutableListOf(),
    val sideBoardMap: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val maybeBoardMap: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val foilMap: MutableMap<String, Int> = mutableMapOf(),
    val format: String = "",
    var lastModifiedDate: String = ""
) {
    private val lastModifiedDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    fun getFormatAsEnum(): FormatEnum {
        return try {
            FormatEnum.valueOf(format)
        } catch (e: IllegalArgumentException) {
            FormatEnum.NONE
        }
    }

    init {
        if (lastModifiedDate.isEmpty()) {
            setNewLastModifiedDate()
        }
    }

    fun setNewLastModifiedDate(time: Date = Calendar.getInstance().time) {
        lastModifiedDate = lastModifiedDateFormat.format(time)
    }
}