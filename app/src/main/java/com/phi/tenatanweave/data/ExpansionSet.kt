package com.phi.tenatanweave.data

import java.text.SimpleDateFormat
import java.util.*

class ExpansionSet(
    val name: String = "",
    val setCode: String = "",
    val releaseDate: String = "",
    val parentSet: String = "",
    val cardCount: String = ""

) {
    val releaseDateFormatDisplay = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    val releaseDateFormatToSort = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    fun getReleaseDateAsDateToSort(): Date? {
        return try {
            releaseDateFormatToSort.parse(releaseDate)
        } catch (e: Exception) {
            releaseDateFormatToSort.parse("2012-12-21T00:00:00")
        }
    }

    fun getReleaseDateAsDateToDisplay(): String? {
        return try {
            releaseDateFormatDisplay.format(releaseDateFormatToSort.parse(releaseDate)!!)
        } catch (e: Exception) {
            releaseDateFormatDisplay.format(releaseDateFormatToSort.parse("2012-12-21T00:00:00")!!)
        }
    }

}