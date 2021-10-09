package com.phi.tenatanweave.data

import com.google.firebase.database.Exclude
import java.text.SimpleDateFormat
import java.util.*

class FullUserCollection(
    val collectionMap: MutableMap<String, MutableMap<String, CollectionEntry>> = mutableMapOf(),
    var lastModifiedDate: String = ""
) {
    private val lastModifiedDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    init {
        if (lastModifiedDate.isEmpty()) {
            setNewLastModifiedDate()
        }
    }

    @Exclude
    fun setNewLastModifiedDate(time: Date = Calendar.getInstance().time){
        lastModifiedDate = lastModifiedDateFormat.format(time)
    }
}