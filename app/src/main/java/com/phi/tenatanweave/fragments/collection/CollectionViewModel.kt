package com.phi.tenatanweave.fragments.collection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.data.Deck
import com.phi.tenatanweave.data.ExpansionSet
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.PrintingWithFinish
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

    val setNameQuery : StringBuilder = StringBuilder()

    init {
    }

    fun setPrintingList(printingList : MutableList<Printing>) {
        mPrintingList.value = printingList
        mPrintingList.notifyObserver()
    }

    fun updateOrAddCollectionEntry(quantity: Int, printing: Printing, finishEnum : FinishEnum) {
        Log.d("CollectionViewModel", "updateOrAddCollectionEntry")
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun setExpansionSetMap(newExpansionSetMap : MutableMap<ExpansionSet, MutableSet<ExpansionSet>>){
        mExpansionSetMap.value?.clear()
        mExpansionSetMap.value?.putAll(newExpansionSetMap)
        mExpansionSetDisplayMap.value?.clear()
        mExpansionSetDisplayMap.value?.putAll(newExpansionSetMap)
        mExpansionSetDisplayMap.notifyObserver()
    }

    fun filterSetByName(searchText: String){
        //Save query for re-filtering
        mExpansionSetDisplayMap.value?.clear()
        setNameQuery.clear()
        setNameQuery.append(searchText.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT))

        for((key, value) in mExpansionSetMap.value!!){
            if(key.name.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT).contains(setNameQuery))
                mExpansionSetDisplayMap.value?.put(key, value)
            else
                for(expansionSet in value)
                    if(expansionSet.name.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT).contains(setNameQuery))
                        mExpansionSetDisplayMap.value?.put(key,value)
        }
        mExpansionSetDisplayMap.notifyObserver()
    }

    fun reset(){
        mExpansionSetDisplayMap.value?.clear()
        setNameQuery.clear()
        mExpansionSetDisplayMap.value?.putAll(mExpansionSetMap.value!!)
        mExpansionSetDisplayMap.notifyObserver()
    }

}