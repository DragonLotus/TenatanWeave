package com.phi.tenatanweave.fragments.decklist

import android.content.Context
import android.util.Log
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

    private val mDeckListCardSearchList = MutableLiveData<MutableList<CardPrinting>>().apply {
        value = mutableListOf()
    }
    val deckListCardSearchList: LiveData<MutableList<CardPrinting>> = mDeckListCardSearchList

    val unsectionedCardPrintingDeckList: MutableList<CardPrinting> = mutableListOf()

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
        sectionedDeckList.value?.clear()
        unsectionedCardPrintingDeckList.clear()

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

        processEquipment(masterCardPrintingList, context)
        processCoreDeck(masterCardPrintingList, context)
    }

    private fun processEquipment(masterCardPrintingList: MutableList<CardPrinting>, context: Context) {
        val equipmentList = mutableListOf<String>()
        mDeck.value?.equipmentMap?.values?.flatten()?.let { equipmentList.addAll(it) }
        val equipmentRecyclerItemList = mutableListOf<RecyclerItem.CardPrinting>()

        for (equipmentId in equipmentList) {
            masterCardPrintingList.first { masterCardPrint -> masterCardPrint.printing.id == equipmentId }
                .let {
                    unsectionedCardPrintingDeckList.add(it)
                    equipmentRecyclerItemList.add(RecyclerItem.CardPrinting(it))
                }
        }

        equipmentRecyclerItemList.sortWith(compareBy { it.cardPrinting.baseCard.name })

        addSectionToList(
            mSectionedDeckList.value!!,
            context.getString(R.string.label_equipment_section, equipmentRecyclerItemList.size),
            equipmentRecyclerItemList
        )
    }

    private fun processCoreDeck(masterCardPrintingList: MutableList<CardPrinting>, context: Context) {
        val coreDeckList = mutableListOf<String>()
        mDeck.value?.coreDeckMap?.values?.flatten()?.let { coreDeckList.addAll(it) }
        val coreDeckCardPrintingSet = mutableSetOf<CardPrinting>()

        for (coreDeckPrintId in coreDeckList) {
            masterCardPrintingList.first { masterCardPrint -> masterCardPrint.printing.id == coreDeckPrintId }
                .let {
                    unsectionedCardPrintingDeckList.add(it)
                    coreDeckCardPrintingSet.add(it)
                }
        }

        val sortedSet =
            coreDeckCardPrintingSet.sortedWith(
                compareBy<CardPrinting> { if (it.baseCard.pitch.isNullOrEmpty()) 0 else it.baseCard.pitch[it.printing.version] }
                    .thenBy { it.baseCard.name })
        mSectionedDeckList.value?.addAll(sortedSet.groupBy {
            if (it.baseCard.pitch.isEmpty()) -1 else it.baseCard.pitch[it.printing.version]
        }.flatMap { (pitchValue, cardPrinting) ->
            listOf<RecyclerItem>(
                RecyclerItem.SetSection(
                    when (pitchValue) {
                        1 -> context.getString(
                            R.string.label_pitch_section,
                            1,
                            getPitchCount(unsectionedCardPrintingDeckList, 1)
                        )
                        2 -> context.getString(
                            R.string.label_pitch_section,
                            2,
                            getPitchCount(unsectionedCardPrintingDeckList, 2)
                        )
                        3 -> context.getString(
                            R.string.label_pitch_section,
                            3,
                            getPitchCount(unsectionedCardPrintingDeckList, 3)
                        )
                        else -> context.getString(
                            R.string.label_pitch_section,
                            0,
                            getPitchCount(unsectionedCardPrintingDeckList, 0)
                        )
                    }
                )
            ) + cardPrinting.map {
                RecyclerItem.CardPrinting(it)
            }
        })
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

    private fun getPitchCount(cardPrintingList: MutableList<CardPrinting>, pitchValue: Int): Int {
        return cardPrintingList.count {
            (pitchValue == if (it.baseCard.pitch.isNullOrEmpty()) 0 else it.baseCard.pitch[it.printing.version])
                    && !(it.baseCard.getTypeAsEnum() == TypeEnum.WEAPON || it.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT)
        }
    }

    private fun getEquipmentCount(cardPrintingList: MutableList<CardPrinting>): Int {
        return cardPrintingList.count { it.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || it.baseCard.getTypeAsEnum() == TypeEnum.WEAPON }
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
                    .thenBy { if (it.baseCard.pitch.isNullOrEmpty()) it.baseCard.pitch[it.printing.version] else 0 }
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
            val cardPrintingListFromMap = cardNameCardPrintingMap[cardNameKey]
            val currentCardPrintingPitch =
                if (cardPrinting.baseCard.pitch.isEmpty()) cardPrinting.baseCard.pitch[cardPrinting.printing.version] else 0

            val pitchInMapList = mutableListOf<Int>()
            for (existingCardPrinting in cardPrintingListFromMap!!) {
                pitchInMapList.add(if (existingCardPrinting.baseCard.pitch.isNullOrEmpty()) 0 else existingCardPrinting.baseCard.pitch[existingCardPrinting.printing.version])
            }

            if (!pitchInMapList.contains(currentCardPrintingPitch))
                cardNameCardPrintingMap[cardNameKey]?.add(cardPrinting)
            else {
                val index = pitchInMapList.indexOfFirst { it == currentCardPrintingPitch }
                cardPrintingListFromMap[index].let { existingCardPrinting ->
                    val existingQuantity =
                        unsectionedCardPrintingDeckList.count { it.printing.id == existingCardPrinting.printing.id }
                    val newQuantity =
                        unsectionedCardPrintingDeckList.count { it.printing.id == cardPrinting.printing.id }
                    if (newQuantity > existingQuantity) {
                        cardNameCardPrintingMap[cardNameKey]?.set(index, cardPrinting)
                    }
                }
            }

        }
    }

    fun checkIfMax(cardPrinting: CardPrinting): Boolean {
        return unsectionedCardPrintingDeckList.count {
            (it.baseCard.name == cardPrinting.baseCard.name)
                    && (if (cardPrinting.baseCard.pitch.isNullOrEmpty()) 0 else cardPrinting.baseCard.pitch[cardPrinting.printing.version]) == (if (it.baseCard.pitch.isNullOrEmpty()) 0 else it.baseCard.pitch[it.printing.version])
        } < 3
    }

    fun increaseQuantity(position: Int, cardPrinting: CardPrinting, context: Context): Int {
        if(checkIfMax(cardPrinting)){
            unsectionedCardPrintingDeckList.add(cardPrinting)

            val pitch =
                if (cardPrinting.baseCard.pitch.isNullOrEmpty()) 0 else cardPrinting.baseCard.pitch[cardPrinting.printing.version]
            val sectionHeader = getSectionHeader(position)

            if (sectionHeader != null) {
                updateSectionHeader(sectionHeader, pitch, context)
            }

            return mSectionedDeckList.value?.indexOf(sectionHeader as RecyclerItem) ?: 0
        }

        return 0;
    }

    fun decreaseQuantity(position: Int, cardPrinting: CardPrinting, context: Context): Int {
        unsectionedCardPrintingDeckList.remove(cardPrinting)

        val pitch =
            if (cardPrinting.baseCard.pitch.isNullOrEmpty()) 0 else cardPrinting.baseCard.pitch[cardPrinting.printing.version]
        val sectionHeader = getSectionHeader(position)

        if (sectionHeader != null) {
            updateSectionHeader(sectionHeader, pitch, context)
        }

        return mSectionedDeckList.value?.indexOf(sectionHeader as RecyclerItem) ?: 0
    }

    private fun getSectionHeader(position: Int): RecyclerItem.SetSection? {
        val sectionedList = mSectionedDeckList.value!!

        sectionedList.let {
            for (i in position downTo 0) {
                if (sectionedList[i] is RecyclerItem.SetSection)
                    return sectionedList[i] as RecyclerItem.SetSection
            }
        }
        return null
    }

    private fun updateSectionHeader(sectionHeader: RecyclerItem.SetSection, pitch: Int = 0, context: Context) {
        sectionHeader.apply {
            when {
                this.setName.contains(context.getString(R.string.label_pitch_section_regex, pitch)) -> setName =
                    context.getString(
                        R.string.label_pitch_section,
                        pitch,
                        getPitchCount(unsectionedCardPrintingDeckList, pitch)
                    )
                this.setName.contains(context.getString(R.string.label_equipment_section_regex, pitch)) -> setName =
                    context.getString(
                        R.string.label_equipment_section,
                        getEquipmentCount(unsectionedCardPrintingDeckList)
                    )
            }
        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
