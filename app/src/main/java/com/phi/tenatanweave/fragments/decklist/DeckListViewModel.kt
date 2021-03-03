package com.phi.tenatanweave.fragments.decklist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.BaseCard
import com.phi.tenatanweave.data.Deck
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.RecyclerItem

class DeckListViewModel : ViewModel() {

    private val mDeck = MutableLiveData<Deck>().apply {
        value = Deck()
    }
    val deck: LiveData<Deck> = mDeck

    private val mSectionedDeckList = MutableLiveData<MutableList<RecyclerItem>>().apply {
        value = mutableListOf()
    }
    val sectionedDeckList: LiveData<MutableList<RecyclerItem>> = mSectionedDeckList

    private val mPitchValueMap = MutableLiveData<MutableMap<String, Int>>().apply {
        value = mutableMapOf()
    }
    val pitchValueMap: LiveData<MutableMap<String, Int>> = mPitchValueMap

    private val mDeckQuantityMap = MutableLiveData<MutableMap<String, Int>>().apply {
        value = mutableMapOf()
    }
    val deckQuantityMap: LiveData<MutableMap<String, Int>> = mDeckQuantityMap

    fun setDeck(deck: Deck) {
        mDeck.value = deck
        mDeck.notifyObserver()
    }

    fun processDeck(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        context: Context
    ) {
        mPitchValueMap.value?.clear()
        mDeckQuantityMap.value?.clear()
        sectionedDeckList.value?.clear()
        sectionedDeckList.value?.addAll(
            listOf(
                RecyclerItem.SetSection(
                    context.getString(
                        R.string.label_hero_section,
                        "${if (mDeck.value?.heroId!!.isNotEmpty()) 1 else 0}"
                    )
                ),
                RecyclerItem.HeroPrinting(printingMap[mDeck.value?.heroId!!])
            )
        )
        sectionedDeckList.value?.addAll(
            listOf(
                RecyclerItem.SetSection(
                    context.getString(
                        R.string.label_equipment_section,
                        getEquipmentCount()
                    )
                )
            ) + getEquipmentPrintings(printingMap)
        )
        sectionedDeckList.value?.addAll(
            getCoreDeckPrintings(printingMap, cardMap, context)
        )
    }

    private fun getEquipmentCount(): Int {
        val equipmentList = mutableListOf<String>()
        mDeck.value?.equipmentMap?.values?.flatten()?.let { equipmentList.addAll(it) }
        return equipmentList.size
    }

    private fun getEquipmentPrintings(printingMap: MutableMap<String, Printing>): MutableList<RecyclerItem.Printing> {
        val equipmentList = mDeck.value?.equipmentMap?.values?.flatten()
        val printingList = mutableListOf<RecyclerItem.Printing>()
        if (equipmentList != null) {
            for (equipment in equipmentList) {
                printingMap[equipment]?.let { RecyclerItem.Printing(it) }?.let { printingList.add(it) }
                mDeckQuantityMap.value?.let {
                    incrementMapAndCheckIfContains(it, equipment)
                }
            }
        }
        printingList.sortWith(compareBy { it.printing.name })
        return printingList
    }

    private fun getCoreDeckPrintings(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        context: Context
    ): MutableList<RecyclerItem> {
        val coreDeckList = mDeck.value?.coreDeckMap?.values?.flatten()
        val printingRedList = mutableListOf<RecyclerItem.Printing>()
        val printingYellowList = mutableListOf<RecyclerItem.Printing>()
        val printingBlueList = mutableListOf<RecyclerItem.Printing>()
        val printingMiscList = mutableListOf<RecyclerItem.Printing>()

        if (coreDeckList != null) {
            for (coreCard in coreDeckList) {
                val printing = printingMap[coreCard]
                val baseCard = cardMap[printing?.name]
                if (printing != null) {
                    if (baseCard?.pitch.isNullOrEmpty()) {
                        mDeckQuantityMap.value?.let {
                            if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                printing.let { cardPrinting -> RecyclerItem.Printing(cardPrinting) }
                                    .let { recyclerItem -> printingMiscList.add(recyclerItem) }
                                mPitchValueMap.value?.set(printing.id, -1)
                            }
                        }
                    } else {
                        when (printing.version.let { baseCard?.pitch?.get(it) }) {
                            1 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printingRedList.add(RecyclerItem.Printing(printing))
                                        mPitchValueMap.value?.set(printing.id, 1)
                                    }
                                }
                            }
                            2 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printingYellowList.add(RecyclerItem.Printing(printing))
                                        mPitchValueMap.value?.set(printing.id, 2)
                                    }
                                }
                            }
                            3 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printingBlueList.add(RecyclerItem.Printing(printing))
                                        mPitchValueMap.value?.set(printing.id, 3)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        printingRedList.sortWith(compareBy { it.printing.name })
        printingYellowList.sortWith(compareBy { it.printing.name })
        printingBlueList.sortWith(compareBy { it.printing.name })
        printingMiscList.sortWith(compareBy { it.printing.name })

        val printingList = mutableListOf<RecyclerItem>()
        if (printingMiscList.isNotEmpty()) {
            addSectionToList(
                printingList,
                context.getString(R.string.label_pitch_misc, printingMiscList.size),
                printingMiscList
            )
        }
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_red, printingRedList.size),
            printingRedList
        )
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_yellow, printingYellowList.size),
            printingYellowList
        )
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_blue, printingBlueList.size),
            printingBlueList
        )

        return printingList
    }

    private fun incrementMapAndCheckIfContains(map: MutableMap<String, Int>, key: String): Boolean {
        return when (val count = map[key]) {
            null -> {
                map[key] = 1
                false
            }
            else -> {
                map[key] = count + 1
                true
            }
        }
    }

    private fun addSectionToList(
        list: MutableList<RecyclerItem>,
        sectionName: String,
        sectionContents: MutableList<RecyclerItem.Printing>
    ) {
        list.addAll(
            listOf(RecyclerItem.SetSection(sectionName)) + sectionContents
        )
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
