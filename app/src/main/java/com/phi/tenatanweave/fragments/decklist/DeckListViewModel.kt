package com.phi.tenatanweave.fragments.decklist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.*
import com.phi.tenatanweave.data.enums.ClassEnum
import com.phi.tenatanweave.data.enums.FormatEnum
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

    private val mDeckQuantityMap = MutableLiveData<MutableMap<String, Int>>().apply {
        value = mutableMapOf()
    }
    val deckQuantityMap: LiveData<MutableMap<String, Int>> = mDeckQuantityMap

    private val mDeckListCardSearchList = MutableLiveData<MutableList<CardPrinting>>().apply {
        value = mutableListOf()
    }
    val deckListCardSearchList: LiveData<MutableList<CardPrinting>> = mDeckListCardSearchList

    val unsectionedCardPrintingDeckList : MutableList<CardPrinting> = mutableListOf()

    fun setDeck(deck: Deck) {
        mDeck.value = deck
        mDeck.notifyObserver()
    }

    fun processDeck(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        masterCardPrintingList: MutableList<CardPrinting>,
        context: Context
    ) {
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
            ) + getEquipmentPrintings(printingMap, cardMap)
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

    private fun getEquipmentPrintings(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
    ): MutableList<RecyclerItem.CardPrinting> {
        val equipmentList = mDeck.value?.equipmentMap?.values?.flatten()
        val printingList = mutableListOf<RecyclerItem.CardPrinting>()
        if (equipmentList != null) {
            for (equipment in equipmentList) {
                printingMap[equipment]?.let { equipmentPrinting ->
                    cardMap[equipmentPrinting.name]?.let { equipmentCard ->
                        RecyclerItem.CardPrinting(
                            CardPrinting(equipmentCard, equipmentPrinting)
                        )
                    }?.let { printingList.add(it) }
                }
                mDeckQuantityMap.value?.let {
                    incrementMapAndCheckIfContains(it, equipment)
                }
            }
        }
        printingList.sortWith(compareBy { it.cardPrinting.printing.name })
        return printingList
    }

    private fun getCoreDeckPrintings(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        context: Context
    ): MutableList<RecyclerItem> {
        val coreDeckList = mDeck.value?.coreDeckMap?.values?.flatten()
        val printingRedList = mutableListOf<RecyclerItem.CardPrinting>()
        val printingYellowList = mutableListOf<RecyclerItem.CardPrinting>()
        val printingBlueList = mutableListOf<RecyclerItem.CardPrinting>()
        val printingMiscList = mutableListOf<RecyclerItem.CardPrinting>()

        if (coreDeckList != null) {
            for (coreCard in coreDeckList) {
                val printing = printingMap[coreCard]
                val baseCard = cardMap[printing?.name]
                if (printing != null) {
                    if (baseCard?.pitch.isNullOrEmpty()) {
                        mDeckQuantityMap.value?.let {
                            if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                printing.let { print ->
                                    cardMap[print.name]?.let { card ->
                                        RecyclerItem.CardPrinting(CardPrinting(card, printing)).let { recyclerItem ->
                                            printingMiscList.add(recyclerItem)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        when (printing.version.let { baseCard?.pitch?.get(it) }) {
                            1 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printing.let { print ->
                                            cardMap[print.name]?.let { card ->
                                                RecyclerItem.CardPrinting(CardPrinting(card, print)).let { recyclerItem ->
                                                    printingRedList.add(recyclerItem)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printing.let { print ->
                                            cardMap[print.name]?.let { card ->
                                                RecyclerItem.CardPrinting(CardPrinting(card, print)).let { recyclerItem ->
                                                    printingYellowList.add(recyclerItem)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> {
                                mDeckQuantityMap.value?.let {
                                    if (!incrementMapAndCheckIfContains(it, printing.id)) {
                                        printing.let { print ->
                                            cardMap[print.name]?.let { card ->
                                                RecyclerItem.CardPrinting(CardPrinting(card, print)).let { recyclerItem ->
                                                    printingBlueList.add(recyclerItem)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        printingRedList.sortWith(compareBy { it.cardPrinting.printing.name })
        printingYellowList.sortWith(compareBy { it.cardPrinting.printing.name })
        printingBlueList.sortWith(compareBy { it.cardPrinting.printing.name })
        printingMiscList.sortWith(compareBy { it.cardPrinting.printing.name })

        val printingList = mutableListOf<RecyclerItem>()
        if (printingMiscList.isNotEmpty()) {
            addSectionToList(
                printingList,
                context.getString(R.string.label_pitch_misc, getPitchCount(printingMiscList, -1)),
                printingMiscList
            )
        }
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_red, getPitchCount(printingRedList, 1)),
            printingRedList
        )
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_yellow, getPitchCount(printingYellowList, 2)),
            printingYellowList
        )
        addSectionToList(
            printingList,
            context.getString(R.string.label_pitch_blue, getPitchCount(printingBlueList, 3)),
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
        sectionContents: MutableList<RecyclerItem.CardPrinting>
    ) {
        list.addAll(
            listOf(RecyclerItem.SetSection(sectionName)) + sectionContents
        )
    }

    private fun getPitchCount(cardPrintingList: MutableList<RecyclerItem.CardPrinting>, pitchValue: Int) : Int{
        return cardPrintingList.count {pitchValue == if(it.cardPrinting.baseCard.pitch.isEmpty()) -1 else it.cardPrinting.baseCard.pitch[it.cardPrinting.printing.version]  }
    }

    fun filterCardsPrioritizingPrintings(masterCardPrintingList: MutableList<CardPrinting>, searchText: String) {
        if (!masterCardPrintingList.isNullOrEmpty()) {
            mDeckListCardSearchList.value?.clear()
            val cardNameCardPrintingMap = mutableMapOf<String, MutableList<CardPrinting>>()
            val heroCardPrinting =
                if (mDeck.value?.heroId.isNullOrEmpty()) null else masterCardPrintingList.first { it.printing.id == mDeck.value?.heroId }

            for (cardPrinting in masterCardPrintingList) {
                val cardNameKey = cardPrinting.baseCard.name
                    .replace("[^a-zA-Z0-9]", "")
                    .toLowerCase(Locale.ROOT)
                if (filterCard(cardPrinting, heroCardPrinting) && cardNameKey.contains(searchText)) {
                    addCardPrintingToMap(cardPrinting, cardNameCardPrintingMap)
                }
            }

            mDeckListCardSearchList.value?.addAll(cardNameCardPrintingMap.values.flatten())
            mDeckListCardSearchList.value?.sortWith(
                compareBy<CardPrinting> { it.baseCard.name }
                    .thenBy { if (it.baseCard.pitch.isNotEmpty()) it.baseCard.pitch[it.printing.version] else -1 }
            )
            mDeckListCardSearchList.notifyObserver()
        }
    }

    private fun filterCard(cardPrinting: CardPrinting, heroCardPrinting: CardPrinting?): Boolean {
        heroCardPrinting?.let {
            if (TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.HERO
                || TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.TOKEN
                || !cardPrinting.baseCard.legalFormats.contains(FormatEnum.valueOf(mDeck.value?.format!!))
            )
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
