package com.phi.tenatanweave.fragments.decklist

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.*
import com.phi.tenatanweave.data.enums.ClassEnum
import com.phi.tenatanweave.data.enums.TalentEnum
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

    private val mDeckListCardSearchList = MutableLiveData<MutableList<Printing>>().apply {
        value = mutableListOf()
    }
    val deckListCardSearchList: LiveData<MutableList<Printing>> = mDeckListCardSearchList

    private val mFormatList = MutableLiveData<MutableList<Format>>().apply {
        value = mutableListOf()
    }
    val formatList: LiveData<MutableList<Format>> = mFormatList

    val unsectionedCardPrintingDeckList: MutableList<Printing> = mutableListOf()
    val notLegalCardSet: MutableSet<String> = mutableSetOf()

    var isHeroSearchMode = false
    val heroList = mutableListOf<Printing>()

    fun setDeck(deck: Deck) {
        mDeck.value = deck
        mDeck.notifyObserver()
    }

    fun processDeck(
        masterCardPrintingList: MutableList<Printing>,
        context: Context
    ) {
        sectionedDeckList.value?.clear()
        unsectionedCardPrintingDeckList.clear()
        notLegalCardSet.clear()

        val heroCardPrinting =
            if (mDeck.value?.heroId?.printingId.isNullOrEmpty()) null
            else
                with(masterCardPrintingList.find { it.id == mDeck.value?.heroId?.printingId }) {
                    if (this?.finishVersion != mDeck.value?.heroId?.finish)
                        this?.let {
                            mDeck.value?.heroId?.finish?.let { finishFromDeck ->
                                Printing(
                                    this.id,
                                    this.name,
                                    this.version,
                                    this.flavorText,
                                    this.rarity,
                                    this.artist,
                                    this.setCode,
                                    this.set,
                                    this.collectorNumber,
                                    this.finishList,
                                    finishFromDeck,
                                    this.baseCard
                                )
                            }
                        }
                    else
                        this
                }

        heroCardPrinting.let {
            if (it != null) {
                if (mDeck.value?.format != "None" && !it.baseCard.legalFormats.contains(mDeck.value?.format))
                    notLegalCardSet.add(it.baseCard.name)
            }
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

        processEquipment(masterCardPrintingList, heroCardPrinting, context)
        processCoreDeck(masterCardPrintingList, heroCardPrinting, context)
        mSectionedDeckList.notifyObserver()
    }

    private fun processEquipment(
        masterCardPrintingList: MutableList<Printing>,
        heroCardPrinting: Printing?,
        context: Context
    ) {
        val equipmentList = mutableListOf<PrintingWithFinish>()
        equipmentList.addAll(mDeck.value?.equipmentList!!)
        val equipmentRecyclerItemSet = mutableSetOf<RecyclerItem.Printing>()

        for (equipment in equipmentList) {
            masterCardPrintingList.first { masterCardPrint -> masterCardPrint.id == equipment.printingId }
                .let {
                    val cardPrinting =
                        if (unsectionedCardPrintingDeckList.find { card -> card.id == equipment.printingId && card.finishVersion == equipment.finish } != null)
                            unsectionedCardPrintingDeckList.first { card -> card.id == equipment.printingId && card.finishVersion == equipment.finish }
                        else Printing(
                            it.id,
                            it.name,
                            it.version,
                            it.flavorText,
                            it.rarity,
                            it.artist,
                            it.setCode,
                            it.set,
                            it.collectorNumber,
                            it.finishList,
                            equipment.finish,
                            it.baseCard
                        )
                    if (!checkIfLegalWithHero(cardPrinting, heroCardPrinting))
                        notLegalCardSet.add(cardPrinting.baseCard.name)
                    unsectionedCardPrintingDeckList.add(cardPrinting)
                    equipmentRecyclerItemSet.add(RecyclerItem.Printing(cardPrinting))
                }
        }

        val sortedSet = equipmentRecyclerItemSet.sortedWith(compareBy { it.printing.baseCard.name })

        addSectionToList(
            mSectionedDeckList.value!!,
            context.getString(R.string.label_equipment_section, equipmentList.size),
            sortedSet.toMutableList()
        )
    }

    private fun processCoreDeck(
        masterCardPrintingList: MutableList<Printing>,
        heroCardPrinting: Printing?,
        context: Context
    ) {
        val coreDeckList = mutableListOf<PrintingWithFinish>()
        coreDeckList.addAll(mDeck.value?.coreDeckList!!)
        val coreDeckCardPrintingSet = mutableSetOf<Printing>()

        for (coreDeckCard in coreDeckList) {
            masterCardPrintingList.first { masterCardPrint -> masterCardPrint.id == coreDeckCard.printingId }
                .let {
                    val cardPrinting =
                        if (unsectionedCardPrintingDeckList.find { card -> card.id == coreDeckCard.printingId && card.finishVersion == coreDeckCard.finish } != null)
                            unsectionedCardPrintingDeckList.first { card -> card.id == coreDeckCard.printingId && card.finishVersion == coreDeckCard.finish }
                        else Printing(
                            it.id,
                            it.name,
                            it.version,
                            it.flavorText,
                            it.rarity,
                            it.artist,
                            it.setCode,
                            it.set,
                            it.collectorNumber,
                            it.finishList,
                            coreDeckCard.finish,
                            it.baseCard
                        )
                    if (!checkIfLegalWithHero(cardPrinting, heroCardPrinting))
                        notLegalCardSet.add(cardPrinting.baseCard.name)
                    unsectionedCardPrintingDeckList.add(cardPrinting)
                    coreDeckCardPrintingSet.add(cardPrinting)
                }
        }

        val sortedSet =
            coreDeckCardPrintingSet.sortedWith(
                compareBy<Printing> { it.getPitchSafe() }
                    .thenBy { it.baseCard.name }
                    .thenByDescending { it.finishVersion })
        mSectionedDeckList.value?.addAll(sortedSet.groupBy {
            it.getPitchSafe()
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
                RecyclerItem.Printing(it)
            }
        })
    }

    private fun incrementMap(map: MutableMap<Printing, Int>, key: Printing) {
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
        sectionContents: MutableList<RecyclerItem.Printing>
    ) {
        list.addAll(
            listOf(RecyclerItem.SetSection(sectionName)) + sectionContents
        )
    }

    private fun getPitchCount(cardPrintingList: MutableList<Printing>, pitchValue: Int): Int {
        return cardPrintingList.count {
            (pitchValue == it.getPitchSafe())
                    && !(it.baseCard.getTypeAsEnum() == TypeEnum.WEAPON || it.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT)
        }
    }

    private fun getEquipmentCount(cardPrintingList: MutableList<Printing>): Int {
        return cardPrintingList.count { it.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || it.baseCard.getTypeAsEnum() == TypeEnum.WEAPON }
    }

    fun setupHeroSearch(masterCardPrintingList: MutableList<Printing>) {
        mDeckListCardSearchList.value?.clear()
        heroList.clear()
        heroList.addAll(masterCardPrintingList.filter {
            if (mDeck.value?.format != "None") {
                it.baseCard.getTypeAsEnum() == TypeEnum.HERO && it.baseCard.legalFormats.contains(
                    mDeck.value?.format
                )
            } else
                it.baseCard.getTypeAsEnum() == TypeEnum.HERO
        }.distinctBy { it.baseCard.name }.sortedWith(compareBy { it.baseCard.name }))
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

    fun filterCardsPrioritizingPrintings(masterCardPrintingList: MutableList<Printing>, searchText: String) {
        if (!masterCardPrintingList.isNullOrEmpty()) {
            mDeckListCardSearchList.value?.clear()
            val cardNameCardPrintingMap = mutableMapOf<String, MutableList<Printing>>()
            val heroCardPrinting =
                if (mDeck.value?.heroId?.printingId.isNullOrEmpty()) null else masterCardPrintingList.first { it.id == mDeck.value?.heroId?.printingId }

            for (cardPrinting in masterCardPrintingList) {
                val cardNameKey = cardPrinting.baseCard.name
                    .replace("[^a-zA-Z0-9]", "")
                    .toLowerCase(Locale.ROOT)
                if (checkIfLegalWithHero(cardPrinting, heroCardPrinting) && cardNameKey.contains(searchText.toLowerCase())) {
                    addCardPrintingToMap(cardPrinting, cardNameCardPrintingMap)
                }
            }

            mDeckListCardSearchList.value?.addAll(cardNameCardPrintingMap.values.flatten())
            mDeckListCardSearchList.value?.sortWith(
                compareBy<Printing> { it.baseCard.name }
                    .thenBy { it.getPitchSafe() }
            )
            mDeckListCardSearchList.notifyObserver()
        }
    }

    private fun checkIfLegalWithHero(cardPrinting: Printing, heroCardPrinting: Printing?): Boolean {
        var legal = true
        heroCardPrinting?.let {
            if (cardPrinting.baseCard.specialization.isNotEmpty() && !cardPrinting.baseCard.specialization.contains(
                    heroCardPrinting.baseCard.name
                )
            )
                legal = false


            if (mDeck.value?.format != "None") {
                if (TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.HERO
                    || TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.TOKEN
                    || !cardPrinting.baseCard.legalFormats.contains(mDeck.value?.format)
                )
                    legal = false
            } else {
                if (TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.HERO
                    || TypeEnum.valueOf(cardPrinting.baseCard.type) == TypeEnum.TOKEN
                )
                    legal = false
            }

            if (cardPrinting.baseCard.getHeroClassAsEnum() != ClassEnum.ALL && cardPrinting.baseCard.getHeroClassAsEnum() != it.baseCard.getHeroClassAsEnum() && cardPrinting.baseCard.getHeroClassAsEnum() != ClassEnum.GENERIC)
                legal = false

            if (cardPrinting.baseCard.getTalentAsEnum() != TalentEnum.ALL && cardPrinting.baseCard.getTalentAsEnum() != it.baseCard.getTalentAsEnum())
                legal = false

            return legal
        }
        return false
    }

    private fun addCardPrintingToMap(
        cardPrinting: Printing,
        cardNameCardPrintingMap: MutableMap<String, MutableList<Printing>>
    ) {
        val cardNameKey = cardPrinting.baseCard.name
            .replace("[^a-zA-Z0-9]", "")
            .toLowerCase(Locale.ROOT)

        val filteredUnsectionedList =
            unsectionedCardPrintingDeckList.filter { it.name == cardPrinting.name && it.version == cardPrinting.version }
        val filteredUnsectionedQuantityMap = mutableMapOf<Printing, Int>()

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
            val currentCardVersion = cardPrinting.version
            val index = cardPrintingListFromMap?.indexOfFirst { it.version == currentCardVersion }
            if (index != -1) {
                if (index?.let { cardPrintingListFromMap[it] } != greatestQuantityCardPrinting)
                    index?.let { cardNameCardPrintingMap[cardNameKey]?.set(it, cardPrinting) }
            } else
                cardNameCardPrintingMap[cardNameKey]?.add(cardPrinting)
        }
    }

    private fun findGreatestQuantityCardPrinting(quantityMap: MutableMap<Printing, Int>): Printing? {
        val greatestQuantity = quantityMap.maxByOrNull { it.value }
        return greatestQuantity?.key
    }

    fun checkIfNotMax(cardPrinting: Printing): Boolean {
        val pitch = cardPrinting.getPitchSafe()
        val count = unsectionedCardPrintingDeckList.count {
            (it.baseCard.name == cardPrinting.baseCard.name)
                    && pitch == (it.getPitchSafe())
        }

        if (cardPrinting.baseCard.deckLimit > 0)
            return count < cardPrinting.baseCard.deckLimit

        val format = formatList.value?.first { it.name == deck.value?.format } ?: Format()
        return if (format.maxCopyLimit > 0)
            count < formatList.value?.first { it.name == deck.value?.format }?.maxCopyLimit ?: 3
        else
            true
    }

    fun checkIfLegal(cardPrinting: Printing): Boolean {
        return !notLegalCardSet.contains(cardPrinting.baseCard.name)
    }

    fun setHero(cardPrinting: Printing) {
        mDeck.value?.heroId = PrintingWithFinish(cardPrinting.id, cardPrinting.finishVersion)
        mDeck.value?.deckPictureId = cardPrinting.id
        mDeck.value?.setNewLastModifiedDate()
        mDeck.notifyObserver()
    }

    fun increaseQuantity(position: Int, cardPrinting: Printing, context: Context): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        if (checkIfNotMax(cardPrinting)) {
            unsectionedCardPrintingDeckList.add(cardPrinting)
            addToDeck(cardPrinting)
            val pitch = cardPrinting.getPitchSafe()

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

    fun decreaseQuantity(position: Int, cardPrinting: Printing, context: Context): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        unsectionedCardPrintingDeckList.remove(cardPrinting)
        removeFromDeck(cardPrinting)
        var countIsZero = false
        val pitch = cardPrinting.getPitchSafe()

        if (unsectionedCardPrintingDeckList.count { it.id == cardPrinting.id } == 0) {
            val removeIndex =
                mSectionedDeckList.value!!.indexOfFirst { it is RecyclerItem.Printing && it.printing.id == cardPrinting.id }
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
        cardPrinting: Printing,
        context: Context
    ): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        if (checkIfNotMax(cardPrinting)) {
            unsectionedCardPrintingDeckList.add(cardPrinting)
            addToDeck(cardPrinting)
            indicesToUpdateList.add(AdapterUpdate.Changed(position))
            mDeck.notifyObserver()
        }
        return indicesToUpdateList
    }

    fun decreaseQuantityFromSearch(
        position: Int,
        cardPrinting: Printing,
        context: Context
    ): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        unsectionedCardPrintingDeckList.remove(cardPrinting)
        removeFromDeck(cardPrinting)
        indicesToUpdateList.add(AdapterUpdate.Changed(position))
        mDeck.notifyObserver()
        return indicesToUpdateList
    }

    private fun findIndicesOfSameNameAndPitch(cardPrinting: Printing): MutableList<AdapterUpdate> {
        val indicesToUpdateList = mutableListOf<AdapterUpdate>()
        for (i in mSectionedDeckList.value!!.indices) {
            with(mSectionedDeckList.value!![i]) {
                if (this is RecyclerItem.Printing) {
                    when {
                        cardPrinting.baseCard.printings.contains(this.printing.id) && cardPrinting.version
                                == this.printing.version -> indicesToUpdateList.add(AdapterUpdate.Changed(i))
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

    private fun addToDeck(cardPrinting: Printing) {
        if (cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.WEAPON)
            mDeck.value?.equipmentList?.add(PrintingWithFinish(cardPrinting.id, cardPrinting.finishVersion))
        else
            mDeck.value?.coreDeckList?.add(PrintingWithFinish(cardPrinting.id, cardPrinting.finishVersion))
        mDeck.value?.setNewLastModifiedDate()
    }

    private fun removeFromDeck(cardPrinting: Printing) {
        if (cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.EQUIPMENT || cardPrinting.baseCard.getTypeAsEnum() == TypeEnum.WEAPON) {
            val index =
                mDeck.value?.equipmentList?.indexOfFirst { it.printingId == cardPrinting.id && it.finish == cardPrinting.finishVersion }
            try {
                if (index != null)
                    mDeck.value?.equipmentList?.removeAt(index)
            } catch (e: Exception) {
                Log.d(
                    "DeckListViewModel",
                    "Cannot find ${cardPrinting.name} to remove from ${mDeck.value?.equipmentList} with index $index."
                )
            }
        } else {
            val index =
                mDeck.value?.coreDeckList?.indexOfFirst { it.printingId == cardPrinting.id && it.finish == cardPrinting.finishVersion }
            try {
                if (index != null)
                    mDeck.value?.coreDeckList?.removeAt(index)
            } catch (e: Exception) {
                Log.d(
                    "DeckListViewModel",
                    "Cannot find ${cardPrinting.name} to remove from ${mDeck.value?.coreDeckList} with index $index."
                )
            }
        }
        mDeck.value?.setNewLastModifiedDate()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
