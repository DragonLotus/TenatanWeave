package com.phi.tenatanweave.fragments.collection

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CollectionEntry
import com.phi.tenatanweave.data.ExpansionSet
import com.phi.tenatanweave.data.FullUserCollection
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.enums.FinishEnum
import java.util.*

class CollectionViewModel : ViewModel() {
    private val mExpansionSetMap = MutableLiveData<MutableMap<ExpansionSet, MutableSet<ExpansionSet>>>().apply {
        value = mutableMapOf()
    }
    val expansionSetMap: LiveData<MutableMap<ExpansionSet, MutableSet<ExpansionSet>>> = mExpansionSetMap

    private val mExpansionSetDisplayMap = MutableLiveData<MutableMap<ExpansionSet, MutableSet<ExpansionSet>>>().apply {
        value = mutableMapOf()
    }
    val expansionSetDisplayMap: LiveData<MutableMap<ExpansionSet, MutableSet<ExpansionSet>>> = mExpansionSetDisplayMap

    private val mPrintingList = MutableLiveData<MutableList<Printing>>().apply {
        value = mutableListOf()
    }
    val printingList: LiveData<MutableList<Printing>> = mPrintingList

    private val mDatabaseDirectory = MutableLiveData<DatabaseReference>().apply {
        value = null
    }
    val databaseDirectory: LiveData<DatabaseReference> = mDatabaseDirectory

    var currentSetCollectionEntryMap: MutableMap<String, CollectionEntry> = mutableMapOf()
    lateinit var fullCollection: FullUserCollection

    val setNameQuery: StringBuilder = StringBuilder()

    init {
    }

    fun setCurrentSetCollectionEntryMap(setCode: String) {
        currentSetCollectionEntryMap.clear()
        fullCollection.collectionMap[setCode]?.let {
            currentSetCollectionEntryMap = it
        }
    }

    fun setPrintingList(printingList: MutableList<Printing>) {
        mPrintingList.value = printingList
        mPrintingList.notifyObserver()
    }

    fun setDatabaseDirectory(db: DatabaseReference) {
        mDatabaseDirectory.value = db
    }

    fun updateOrAddCollectionEntry(quantity: Int, printing: Printing, finishEnum: FinishEnum, resources: Resources) {

        val collectionDatabase = databaseDirectory.value?.child(resources.getString(R.string.db_collection_collection))
        fullCollection.setNewLastModifiedDate()
        if (currentSetCollectionEntryMap.keys.contains(printing.id)) {
            val existingEntry = currentSetCollectionEntryMap[printing.id]
            existingEntry?.let{
                updateQuantityList(it, printing, finishEnum, quantity)

                val childUpdates = hashMapOf<String, Any>(
                    "/${resources.getString(R.string.db_collection_collection_map)}/${printing.setCode}/${printing.id}" to existingEntry,
                    "/lastModifiedDate" to fullCollection.lastModifiedDate
                )

                collectionDatabase?.updateChildren(childUpdates)
            }

        } else {
            val newEntry = CollectionEntry(
                printing.collectorNumber,
                printing.id,
                printing.setCode,
                createNewQuantityList(printing, finishEnum, quantity)
            )
            currentSetCollectionEntryMap[printing.id] = newEntry

            val childUpdates = hashMapOf<String, Any>(
                "/${resources.getString(R.string.db_collection_collection_map)}/${printing.setCode}/${printing.id}" to newEntry,
                "/lastModifiedDate" to fullCollection.lastModifiedDate
            )

            collectionDatabase?.updateChildren(childUpdates)
            Log.d("CollectionViewModel", "updateOrAddCollectionEntry adding new entry: $newEntry")
        }
    }

    private fun createNewQuantityList(printing: Printing, finishEnum: FinishEnum, quantity: Int): MutableList<Int> {
        val quantityList = MutableList(printing.finishList.size) { 0 }
        val finishIndex = printing.finishList.indexOfFirst { it == finishEnum }
        if (finishIndex < quantityList.size) {
            quantityList[finishIndex] = quantity
        }
        return quantityList
    }

    private fun updateQuantityList(
        collectionEntry: CollectionEntry,
        printing: Printing,
        finishEnum: FinishEnum,
        quantity: Int
    ) {
        val finishIndex = printing.finishList.indexOfFirst { it == finishEnum }
        collectionEntry.quantityList[finishIndex] = quantity
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun setExpansionSetMap(newExpansionSetMap: MutableMap<ExpansionSet, MutableSet<ExpansionSet>>) {
        mExpansionSetMap.value?.clear()
        mExpansionSetMap.value?.putAll(newExpansionSetMap)
        mExpansionSetDisplayMap.value?.clear()
        mExpansionSetDisplayMap.value?.putAll(newExpansionSetMap)
        mExpansionSetDisplayMap.notifyObserver()
    }

    fun filterSetByName(searchText: String) {
        //Save query for re-filtering
        mExpansionSetDisplayMap.value?.clear()
        setNameQuery.clear()
        setNameQuery.append(searchText.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT))

        for ((key, value) in mExpansionSetMap.value!!) {
            if (key.name.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT).contains(setNameQuery))
                mExpansionSetDisplayMap.value?.put(key, value)
            else
                for (expansionSet in value)
                    if (expansionSet.name.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT).contains(setNameQuery))
                        mExpansionSetDisplayMap.value?.put(key, value)
        }
        mExpansionSetDisplayMap.notifyObserver()
    }

    fun reset() {
        mExpansionSetDisplayMap.value?.clear()
        setNameQuery.clear()
        mExpansionSetDisplayMap.value?.putAll(mExpansionSetMap.value!!)
        mExpansionSetDisplayMap.notifyObserver()
    }

}