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

    var isHeroSearchMode = false
    val heroList = mutableListOf<CardPrinting>()

    fun setDeck(deck: Deck) {
        mDeck.value = deck
        mDeck.notifyObserver()
    }

    fun processDeck(
        masterCardPrintingList: MutableList<CardPrinting>,
        context: Context
    ) {
        sectionedDeckList.value?.clear()
        unsectionedCardPrintingDeckList.clear()

        val heroCardPrinting =
            if (mDeck.value?.heroId?.printingId.isNullOrEmpty()) null
            else
                with(masterCardPrintingList.find { it.printing.id == mDeck.value?.heroId?.printingId }) {
                    if (this?.finish != mDeck.value?.heroId?.finish)
                        this?.let {
                            mDeck.value?.heroId?.finish?.let { finishFromDeck ->
                                CardPrinting(
                                    it.baseCard, this.printing,
                                    finishFromDeck
                                )
                            }
                        }
                    else
                        this
                }

        sectionedDeckList.value?.addAll(
            listOf(
                RecyclerItem.SetSection(
                    context.getString(
                        R.string.label_hero_section,
                        "${if (mDeck.value?.heroId?.printingId.isNullOrEmpty()) 0 else 1}"
                    )
                ),
                RecyclerItem.HeroPrinting(heroCardPrinting)
            )
        )

        processEquipment(masterCardPrintingList, context)
        processCoreDeck(masterCardPrintingList, context)
        mSectionedDeckList.notifyObserver()
    }

    private fun processEquipment(masterCardPrintingList: MutableList<CardPrinting>, context: Context) {
        val equipmentList = mutableListOf<PrintingWithFinish>()
        equipmentList.addAll(mDeck.value?.equipmentList!!)
        val equipmentRecyclerItemSet = mutableSetOf<RecyclerItem.CardPrinting>()

        for (equipment in equipmentList) {
            masterCardPrintingList.first { masterCardPrint -> masterCardPrint.printing.id == equipment.printingId }
                .let {
                    val cardPrinting =
                        if (unsectionedCardPrintingDeckList.find { card -> card.printing.id == equipment.printingId && card.finish == equipment.finish } != null)
                            unsectionedCardPrintingDeckList.first { card -> card.printing.id == equipment.printingId && card.finish == equipment.finish }
                        else CardPrinting(
                            it.baseCard,
                            it.printing,
                            equipment.finish
                        )
                    unsectionedCardPrintingDeckList.add(cardPrinting)
                    equipmentRecyclerItemSet.add(RecyclerItem.CardPrinting(cardPrinting))
                }
        }

        val sortedSet = equipmentRecyclerItemSet.sortedWith(compareBy { it.cardPrinting.baseCard.name })

        addSectionToList(
            mSectionedDeckList.value!!,
            context.getString(R.string.label_equipment_section, equipmentRecyclerItemSet.size),
            sortedSet.toMutableList()
        )
    }

    private fun processCoreDeck(masterCardPrintingList: MutableList<CardPrinting>, context: Context) {
        val coreDeckList = mutableListOf<PrintingWithFinish>()
        coreDeckList.addAll(mDeck.value?.coreDeckList!!)
        val coreDeckCardPrintingSet = mutableSetOf<CardPrinting>()

        for (coreDeckCard in coreDeckList) {
            masterCardPrintingList.first { masterCardPrint -> masterCardPrint.printing.id == coreDeckCard.printingId }
                .let {
                    val cardPrinting =
                        if (unsectionedCardPrintingDeckList.find { card -> card.printing.id == coreDeckCard.printingId && card.finish == coreDeckCard.finish } != null)
                            unsectionedCardPrintingDeckList.first { card -> card.printing.id == coreDeckCard.printingId && card.finish == coreDeckCard.finish }
                        else CardPrinting(
                            it.baseCard,
                            it.printing,
                            coreDeckCard.finish
                        )
                    unsectionedCardPrintingDeckList.add(cardPrinting)
                    coreDeckCardPrintingSet.add(cardPrinting)
                }
        }

        val sortedSet =
            coreDeckCardPrintingSet.sortedWith(
                compareBy<CardPrinting> { it.baseCard.getPitchSafe(it.printing.version) }
                    .thenBy { it.baseCard.name }
                    .thenByDescending { it.finish })
        mSectionedDeckList.value?.addAll(sortedSet.groupBy {
            it.baseCard.getPitchSafe(it.printing.version)
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

    private fun incrementMap(map: MutableMap<CardPrinting, Int>, key: CardPrinting) {
        return when (val count = map[key]) {
            null -> {
                map[key] = 1
            }
            else -> {
                map[key] = count + 1
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
            (pitchValue == it.baseCard.getPitchSafe(it.printing.version))
                    && !(it.baseCard.getTypeAsEnum() == TypeEnum.WEAPON || it.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT)
        }
    }

    private fun getEquipmentCount(cardPrintingList: MutableList<CardPrinting>): Int {
        return cardPrintingList.count { it.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || it.baseCard.getTypeAsEnum() == TypeEnum.WEAPON }
    }

    fun setupHeroSearch(masterCardPrintingList: MutableList<CardPrinting>) {
        mDeckListCardSearchList.value?.clear()
        if (heroList.isEmpty())
            heroList.addAll(masterCardPrintingList.filter {
                it.baseCard.getTypeAsEnum() == TypeEnum.HERO && it.baseCard.legalFormats.contains(
                    mDeck.value?.getFormatAsEnum()
                )
            }.distinctBy { it.baseCard.name })
        mDeckListCardSearchList.value?.addAll(heroList)
        mDeckListCardSearchList.notifyObserver()
    }

    fun filterHeroCards(searchText: String) {
        mDeckListCardSearchList.value?.clear()
        val filteredList = heroList.filter {
            it.baseCard.name
                .replace("[^a-zA-Z0-9]", "")
                .toLowerCase(Locale.ROOT).contains(
                    searchText
                        .replace("[^a-zA-Z0-9]", "")
                        .toLowerCase(Locale.ROOT)
                )
        }
        mDeckListCardSearchList.value?.addAll(filteredList)
        mDeckListCardSearchList.notifyObserver()
    }

    fun filterCardsPrioritizingPrintings(masterCardPrintingList: MutableList<CardPrinting>, searchText: String) {
        if (!masterCardPrintingList.isNullOrEmpty()) {
            mDeckListCardSearchList.value?.clear()
            val cardNameCardPrintingMap = mutableMapOf<String, MutableList<CardPrinting>>()
            val heroCardPrinting =
                if (mDeck.value?.heroId?.printingId.isNullOrEmpty()) null else masterCardPrintingList.first { it.printing.id == mDeck.value?.heroId?.printingId }

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
                    .thenBy { it.baseCard.getPitchSafe(it.printing.version) }
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

        val filteredUnsectionedList =
            unsectionedCardPrintingDeckList.filter { it.printing.name == cardPrinting.printing.name && it.printing.version == cardPrinting.printing.version }
        val filteredUnsectionedQuantityMap = mutableMapOf<CardPrinting, Int>()

        if (filteredUnsectionedList.isNotEmpty()) {
            for (filteredCardPrinting in filteredUnsectionedList) {
                incrementMap(filteredUnsectionedQuantityMap, filteredCardPrinting)
            }
        }

        val greatestQuantityCardPrinting =
            findGreatestQuantityCardPrinting(filteredUnsectionedQuantityMap) ?: cardPrinting

        if (cardNameCardPrintingMap[cardNameKey] == null) {
            cardNameCardPrintingMap[cardNameKey] = mutableListOf(greatestQuantityCardPrinting)
        } else if (cardNameCardPrintingMap[cardNameKey]?.isNotEmpty() == true) {
            val cardPrintingListFromMap = cardNameCardPrintingMap[cardNameKey]
            val currentCardVersion = cardPrinting.printing.version
            val index = cardPrintingListFromMap?.indexOfFirst { it.printing.version == currentCardVersion }
            if (index != -1) {
                if (index?.let { cardPrintingListFromMap[it] } != greatestQuantityCardPrinting)
                    index?.let { cardNameCardPrintingMap[cardNameKey]?.set(it, cardPrinting) }
            } else
                cardNameCardPrintingMap[cardNameKey]?.add(cardPrinting)
        }
    }

    private fun findGreatestQuantityCardPrinting(quantityMap: MutableMap<CardPrinting, Int>): CardPrinting? {
        val greatestQuantity = quantityMap.maxByOrNull { it.value }
        return greatestQuantity?.key
    }

    fun checkIfMax(cardPrinting: CardPrinting): Boolean {
        val pitch = cardPrinting.baseCard.getPitchSafe(cardPrinting.printing.version)
        val count = unsectionedCardPrintingDeckList.count {
            (it.baseCard.name == cardPrinting.baseCard.name)
                    && pitch == (it.baseCard.getPitchSafe(it.printing.version))
        }

        if (cardPrinting.baseCard.deckLimit > 0)
            return count < cardPrinting.baseCard.deckLimit

        return count < 3
    }

    fun setHero(cardPrinting: CardPrinting) {
        mDeck.value?.heroId = PrintingWithFinish(cardPrinting.printing.id, cardPrinting.finish)
        mDeck.value?.deckPictureId = cardPrinting.printing.id
        mDeck.value?.setNewLastModifiedDate()
        mDeck.notifyObserver()
    }

    fun increaseQuantity(position: Int, cardPrinting: CardPrinting, context: Context): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        if (checkIfMax(cardPrinting)) {
            unsectionedCardPrintingDeckList.add(cardPrinting)
            addToDeck(cardPrinting)
            val pitch = cardPrinting.baseCard.getPitchSafe(cardPrinting.printing.version)

            indicesToUpdateList.addAll(findIndicesOfSameNameAndPitch(cardPrinting))

            val sectionHeader = getSectionHeader(position)

            if (sectionHeader != null) {
                updateSectionHeader(sectionHeader, pitch, context)
                mSectionedDeckList.value?.indexOf(sectionHeader as RecyclerItem)?.let {
                    indicesToUpdateList.add(AdapterUpdate.Changed(it))
                }
            }
        }
        return indicesToUpdateList
    }

    fun decreaseQuantity(position: Int, cardPrinting: CardPrinting, context: Context): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        unsectionedCardPrintingDeckList.remove(cardPrinting)
        removeFromDeck(cardPrinting)
        var countIsZero = false
        val pitch = cardPrinting.baseCard.getPitchSafe(cardPrinting.printing.version)

        if (unsectionedCardPrintingDeckList.count { it.printing.id == cardPrinting.printing.id } == 0) {
            val removeIndex =
                mSectionedDeckList.value!!.indexOfFirst { it is RecyclerItem.CardPrinting && it.cardPrinting.printing.id == cardPrinting.printing.id }
            mSectionedDeckList.value!!.removeAt(removeIndex)
            indicesToUpdateList.add(AdapterUpdate.Remove(removeIndex))
            countIsZero = true
        }

        indicesToUpdateList.addAll(findIndicesOfSameNameAndPitch(cardPrinting))

        val sectionHeader = if (countIsZero) getSectionHeader(position - 1) else getSectionHeader(position)

        if (sectionHeader != null) {
            updateSectionHeader(sectionHeader, pitch, context)
            mSectionedDeckList.value?.indexOf(sectionHeader as RecyclerItem)?.let {
                indicesToUpdateList.add(AdapterUpdate.Changed(it))
            }
        }
        return indicesToUpdateList
    }

    fun increaseQuantityFromSearch(
        position: Int,
        cardPrinting: CardPrinting,
        context: Context
    ): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        if (checkIfMax(cardPrinting)) {
            unsectionedCardPrintingDeckList.add(cardPrinting)
            addToDeck(cardPrinting)
            indicesToUpdateList.add(AdapterUpdate.Changed(position))
            mDeck.notifyObserver()
        }
        return indicesToUpdateList
    }

    fun decreaseQuantityFromSearch(
        position: Int,
        cardPrinting: CardPrinting,
        context: Context
    ): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        unsectionedCardPrintingDeckList.remove(cardPrinting)
        removeFromDeck(cardPrinting)
        indicesToUpdateList.add(AdapterUpdate.Changed(position))
        mDeck.notifyObserver()
        return indicesToUpdateList
    }

    private fun findIndicesOfSameNameAndPitch(cardPrinting: CardPrinting): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        for (i in mSectionedDeckList.value!!.indices) {
            with(mSectionedDeckList.value!![i]) {
                if (this is RecyclerItem.CardPrinting) {
                    when {
                        cardPrinting.baseCard.printings.contains(this.cardPrinting.printing.id) && cardPrinting.printing.version
                                == this.cardPrinting.printing.version -> indicesToUpdateList.add(AdapterUpdate.Changed(i))
                    }
                }
            }
        }
        return indicesToUpdateList
    }

    private fun getSectionHeader(position: Int): RecyclerItem.SetSection? {
        val sectionedList = mSectionedDeckList.value!!

        sectionedList.let {
            for (i in position downTo 0) {
                if (it[i] is RecyclerItem.SetSection)
                    return it[i] as RecyclerItem.SetSection
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

    private fun addToDeck(cardPrinting: CardPrinting) {
        if (cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.WEAPON)
            mDeck.value?.equipmentList?.add(PrintingWithFinish(cardPrinting.printing.id, cardPrinting.finish))
        else
            mDeck.value?.coreDeckList?.add(PrintingWithFinish(cardPrinting.printing.id, cardPrinting.finish))
        mDeck.value?.setNewLastModifiedDate()
    }

    private fun removeFromDeck(cardPrinting: CardPrinting) {
        if (cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.WEAPON) {
            val index =
                mDeck.value?.equipmentList?.indexOfFirst { it.printingId == cardPrinting.printing.id && it.finish == cardPrinting.finish }
            try {
                if (index != null)
                    mDeck.value?.equipmentList?.removeAt(index)
            } catch (e: Exception) {
                Log.d(
                    "DeckListViewModel",
                    "Cannot find ${cardPrinting.printing.name} to remove from ${mDeck.value?.equipmentList} with index $index."
                )
            }
        } else {
            val index =
                mDeck.value?.coreDeckList?.indexOfFirst { it.printingId == cardPrinting.printing.id && it.finish == cardPrinting.finish }
            try {
                if (index != null)
                    mDeck.value?.coreDeckList?.removeAt(index)
            } catch (e: Exception) {
                Log.d(
                    "DeckListViewModel",
                    "Cannot find ${cardPrinting.printing.name} to remove from ${mDeck.value?.coreDeckList} with index $index."
                )
            }
        }
        mDeck.value?.setNewLastModifiedDate()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
