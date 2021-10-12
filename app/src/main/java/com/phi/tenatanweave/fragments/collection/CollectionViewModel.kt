package com.phi.tenatanweave.fragments.collection

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

    private val mFullUserCollection = MutableLiveData<FullUserCollection>().apply {
        value = null
    }
    val fullUserCollection: LiveData<FullUserCollection> = mFullUserCollection

    val currentSetCollectionEntryMap: MutableMap<String, CollectionEntry> = mutableMapOf()
    var currentSetCode: String = ""

    val setNameQuery: StringBuilder = StringBuilder()

    init {
    }

    fun setCurrentSetCollectionEntryMap(setCode: String) {
        currentSetCollectionEntryMap.clear()
        fullUserCollection.value?.collectionMap?.get(setCode)?.let {
            currentSetCollectionEntryMap.putAll(it)
        }
    }

    fun setFullUserCollection(fullUserCollection: FullUserCollection) {
        mFullUserCollection.value = fullUserCollection
        mFullUserCollection.notifyObserver()
    }

    fun setPrintingList(printingList: MutableList<Printing>) {
        mPrintingList.value = printingList
        mPrintingList.notifyObserver()
    }

    fun setDatabaseDirectory(db: DatabaseReference) {
        mDatabaseDirectory.value = db
        mDatabaseDirectory.notifyObserver()
    }

    fun refreshFullUserCollection(resources: Resources): MutableLiveData<FullUserCollection> {
        if(mFullUserCollection.value == null){
            val user = Firebase.auth.currentUser
            user?.uid?.let {
                Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(it)
                    .child(resources.getString(R.string.db_collection_collection))
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                setFullUserCollection(snapshot.getValue(FullUserCollection::class.java)!!)
                            } catch (e: Exception) {
                                snapshot.key?.let { key -> Log.d("CollectionViewModel", "Collection Data key is: $key") }
                                Log.d("Exception", e.stackTraceToString())
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
            }
        }
        return mFullUserCollection
    }

    fun updateOrAddCollectionEntry(quantity: Int, printing: Printing, finishEnum: FinishEnum, resources: Resources) {
        val collectionDatabase = databaseDirectory.value?.child(resources.getString(R.string.db_collection_collection))
        fullUserCollection.value?.setNewLastModifiedDate()
        if (currentSetCollectionEntryMap.keys.contains(printing.id)) {
            val existingEntry = currentSetCollectionEntryMap[printing.id]
            existingEntry?.let {
                updateQuantityList(it, printing, finishEnum, quantity)

                val childUpdates = hashMapOf<String, Any>(
                    "/${resources.getString(R.string.db_collection_collection_map)}/${printing.setCode}/${printing.id}" to existingEntry,
                )

                fullUserCollection.value?.let { fullUserCollection ->
                    childUpdates.put("/lastModifiedDate", fullUserCollection.lastModifiedDate)
                }

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
            )

            fullUserCollection.value?.let { fullUserCollection ->
                childUpdates.put("/lastModifiedDate", fullUserCollection.lastModifiedDate)
            }

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