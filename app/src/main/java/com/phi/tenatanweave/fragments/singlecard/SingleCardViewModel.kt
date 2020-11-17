package com.phi.tenatanweave.fragments.singlecard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.data.*

class SingleCardViewModel : ViewModel() {

    private val mCurrentPosition = MutableLiveData<Int>().apply {
        value = 0
    }
    val currentPosition: LiveData<Int> = mCurrentPosition

    private val mCardPrinting = MutableLiveData<CardPrinting>().apply {
        value = null
    }
    val cardPrinting: LiveData<CardPrinting> = mCardPrinting

    private val mRulingMap = MutableLiveData<MutableMap<String, Ruling>>().apply {
        value = mutableMapOf()
    }
    val rulingMap: LiveData<MutableMap<String, Ruling>> = mRulingMap

    private val mSelectedRuling = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }
    val selectedRuling: LiveData<MutableList<String>> = mSelectedRuling

    private val mSelectedPrintingList = MutableLiveData<MutableList<Printing>>().apply {
        value = mutableListOf()
    }
    private val mSectionedPrintingList = MutableLiveData<MutableList<RecyclerItem>>().apply {
        value = mutableListOf()
    }
    val sectionedPrintings: LiveData<MutableList<RecyclerItem>> = mSectionedPrintingList

    private val mSelectedVersions = MutableLiveData<MutableMap<Int, CardPrinting>>().apply {
        value = mutableMapOf()
    }
    val selectedVersions: LiveData<MutableMap<Int, CardPrinting>> = mSelectedVersions

    init {

    }

    fun setCardPrinting(
        cardPrinting: CardPrinting,
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        setMap: MutableMap<String, ExpansionSet>,
        sameCard: Boolean
    ) {
        mCardPrinting.value = cardPrinting
        if (!sameCard) {
            setRuling(cardPrinting)
            setVersions(cardPrinting, printingMap, cardMap)
        }
        setPrintings(cardPrinting, printingMap, setMap)
        mCardPrinting.notifyObserver()
    }

    private fun setRuling(cardPrinting: CardPrinting) {
        mSelectedRuling.value?.clear()
        if (mRulingMap.value?.get(cardPrinting.baseCard.name.replace(".", "")) != null) {
            mSelectedRuling.value?.addAll(mRulingMap.value?.get(cardPrinting.baseCard.name.replace(".", ""))?.rulings!!)
        }
    }

    private fun setPrintings(
        cardPrinting: CardPrinting,
        printingMap: MutableMap<String, Printing>,
        setMap: MutableMap<String, ExpansionSet>
    ) {
        mSelectedPrintingList.value?.clear()
        for (printing in cardPrinting.baseCard.printings) {
            printingMap[printing]?.let {
                if (it.version == cardPrinting.printing.version)
                    mSelectedPrintingList.value?.add(it)
            }
        }
        mSectionedPrintingList.value?.clear()
        mSectionedPrintingList.value?.addAll(mSelectedPrintingList.value!!.groupBy { it.setCode }
            .flatMap { (setCode, printing) ->
                listOf<RecyclerItem>(RecyclerItem.SetSection(printing[0].set!!.name)) + printing.map {
                    RecyclerItem.Printing(it)
                }
            })
    }

    private fun setVersions(
        cardPrinting: CardPrinting,
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>
    ) {
        mSelectedVersions.value?.clear()
        for (version in cardPrinting.baseCard.printings) {
            printingMap[version]?.let { printing ->
                if (mSelectedVersions.value?.get(printing.version) == null)
                    cardMap[printing.name.replace(".", "")]?.let { baseCard ->
                        mSelectedVersions.value?.set(printing.version, CardPrinting(baseCard, printing))
                    }
            }
        }
    }

    fun setCurrentPosition(position: Int) {
        mCurrentPosition.value = position
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}