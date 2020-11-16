package com.phi.tenatanweave.viewpagers.searchviewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.data.enums.SetEnum

class SearchSetViewPagerViewModel : ViewModel() {

    private val mSetMasterList = SetEnum.values().toMutableList()

    private val mSetDisplayList = MutableLiveData<MutableList<SetEnum>>().apply {
        value = mutableListOf()
    }
    val setList: LiveData<MutableList<SetEnum>> = mSetDisplayList

    val setNameQuery : StringBuilder = StringBuilder()

    init {
        mSetDisplayList.value?.addAll(mSetMasterList)
        mSetDisplayList.notifyObserver()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun filterSetByName(searchText: String){
        //Save query for re-filtering
        mSetDisplayList.value?.clear()
        setNameQuery.clear()
        setNameQuery.append(searchText.replace("[^a-zA-Z0-9]", "").toLowerCase())
        for(set in mSetMasterList){
            if(set.toString().replace("[^a-zA-Z0-9]", "").toLowerCase().contains(setNameQuery))
                mSetDisplayList.value?.add(set)
        }
        mSetDisplayList.notifyObserver()
    }

    fun reset(){
        mSetDisplayList.value?.clear()
        setNameQuery.clear()
        mSetDisplayList.value?.addAll(mSetMasterList)
        mSetDisplayList.notifyObserver()
    }

}