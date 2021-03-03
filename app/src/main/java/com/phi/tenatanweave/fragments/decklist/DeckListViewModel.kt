package com.phi.tenatanweave.fragments.decklist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.*
import com.phi.tenatanweave.data.enums.ClassEnum
import com.phi.tenatanweave.data.enums.TypeEnum
import java.util.*

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

    private val mDeckListCardSearchList = MutableLiveData<MutableList<CardPrinting>>().apply {
        value = mutableListOf()
    }
    val deckListCardSearchList: LiveData<MutableList<CardPrinting>> = mDeckListCardSearchList

    var redPitchQuantity = 0
    var yellowPitchQuantity = 0
    var bluePitchQuantity = 0
    var miscPitchQuantity = 0

    fun setDeck(deck: Deck) {
        mDeck.value = deck
        mDeck.notifyObserver()
    }

    fun processDeck(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        context: Context
    ) {
        redPitchQuantity = 0
        yellowPitchQuantity = 0
        bluePitchQuantity = 0
        miscPitchQuantity = 0
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
                        miscPitchQuantity++
                    } else {
                        when (printing.version.let { baseCard?.pitch?.get(it) }) {
                            1 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printingRedList.add(RecyclerItem.Printing(printing))
                                        mPitchValueMap.value?.set(printing.id, 1)
                                    }
                                }
                                redPitchQuantity++
                            }
                            2 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printingYellowList.add(RecyclerItem.Printing(printing))
                                        mPitchValueMap.value?.set(printing.id, 2)
                                    }
                                }
                                yellowPitchQuantity++
                            }
                            3 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printingBlueList.add(RecyclerItem.Printing(printing))
                                        mPitchValueMap.value?.set(printing.id, 3)
                                    }
                                }
                                bluePitchQuantity++
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
                context.getString(R.string.label_pitch_misc, miscPitchQuantity),
                printingMiscList
            )
        }
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_red, redPitchQuantity),
            printingRedList
        )
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_yellow, yellowPitchQuantity),
            printingYellowList
        )
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_blue, bluePitchQuantity),
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

    fun filterCardsPrioritizingPrintings(masterCardPrintingList: MutableList<CardPrinting>, searchText: String) {
        if (!masterCardPrintingList.isNullOrEmpty()) {
            mDeckListCardSearchList.value?.clear()
            val cardNameCardPrintingMap = mutableMapOf<String, MutableList<CardPrinting>>()
            val heroCardPrinting = if(mDeck.value?.heroId.isNullOrEmpty()) null else masterCardPrintingList.first { it.printing.id == mDeck.value?.heroId }

            for (cardPrinting in masterCardPrintingList) {
                val cardNameKey = cardPrinting.baseCard.name
                    .replace("[^a-zA-Z0-9]", "")
                    .toLowerCase(Locale.ROOT)
                if (filterCard(cardPrinting, heroCardPrinting) && cardNameKey.contains(searchText)) {
                    addCardPrintingToMap(cardPrinting, cardNameCardPrintingMap)
                }
            }

            mDeckListCardSearchList.value?.addAll(cardNameCardPrintingMap.values.flatten())
            mDeckListCardSearchList.notifyObserver()
        }
    }

    private fun filterCard(cardPrinting: CardPrinting, heroCardPrinting: CardPrinting?): Boolean {
        heroCardPrinting?.let {
            if (TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.HERO)
                return false

            if (ClassEnum.valueOf(cardPrinting.baseCard.heroClass) == ClassEnum.valueOf(it.baseCard.heroClass)
                || ClassEnum.valueOf(cardPrinting.baseCard.heroClass) == ClassEnum.GENERIC
            )
                return true
        }
        return false
    }

    private fun addCardPrintingToMap(
        cardPrinting: CardPrinting,
        cardNameCardPrintingMap: MutableMap<String, MutableList<CardPrinting>>
    ) {
        val cardNameKey = cardPrinting.baseCard.name
            .replace("[^a-zA-Z0-9]", "")
            .toLowerCase(Locale.ROOT)

        if (cardNameCardPrintingMap[cardNameKey] == null) {
            cardNameCardPrintingMap[cardNameKey] = mutableListOf(cardPrinting)
        } else if (cardNameCardPrintingMap[cardNameKey]?.isNotEmpty() == true) {
            val cardPrintingList = cardNameCardPrintingMap[cardNameKey]
            val currentCardPrintingPitch =
                if (cardPrinting.baseCard.pitch.isNotEmpty()) cardPrinting.baseCard.pitch[cardPrinting.printing.version] else -1

            val pitchInMapList = mutableListOf<Int>()
            for (existingCardPrinting in cardPrintingList!!) {
                pitchInMapList.add(if (existingCardPrinting.baseCard.pitch.isNotEmpty()) existingCardPrinting.baseCard.pitch[existingCardPrinting.printing.version] else -1)
            }

            if (!pitchInMapList.contains(currentCardPrintingPitch))
                cardNameCardPrintingMap[cardNameKey]?.add(cardPrinting)
            else {
                cardPrintingList[pitchInMapList.indexOfFirst { it == currentCardPrintingPitch }].let {
                    val existingQuantity =
                        if (mDeckQuantityMap.value?.get(it.printing.id) == null) 0 else mDeckQuantityMap.value?.get(
                            it.printing.id
                        )
                    val newQuantity =
                        if (mDeckQuantityMap.value?.get(cardPrinting.printing.id) == null) 0 else mDeckQuantityMap.value?.get(
                            cardPrinting.printing.id
                        )
                    if (newQuantity!! > existingQuantity!!) {
                        cardNameCardPrintingMap[cardNameKey]?.add(cardPrinting)
                    }
                }
            }

        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
