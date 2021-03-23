package com.phi.tenatanweave.fragments.searchcardresult

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.data.*
import com.phi.tenatanweave.data.enums.*
import java.util.*

class SearchCardResultViewModel : ViewModel() {
    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    //Begin Search Options
    private val mClassList = MutableLiveData<MutableList<ClassEnum>>().apply {
        value = ClassEnum.values().toMutableList()
    }
    val classList: LiveData<MutableList<ClassEnum>> = mClassList
    val classSelection = MutableLiveData<Int>()
    val includeGenerics = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val mCardTextList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }
    val cardTextList: LiveData<MutableList<String>> = mCardTextList

    private val mPitchList = MutableLiveData<MutableList<Boolean>>().apply {
        value = mutableListOf(false, false, false, false)
    }
    val pitchList: LiveData<MutableList<Boolean>> = mPitchList

    private var mTypeList = MutableLiveData<MutableList<FilterStateEnum>>().apply {
        value = mutableListOf()
        for (i in TypeEnum.values().indices)
            value!!.add(FilterStateEnum.NONE)
        value!![0] = FilterStateEnum.IS
    }
    val typeList: LiveData<MutableList<FilterStateEnum>> = mTypeList

    private var mSubTypeList = MutableLiveData<MutableList<FilterStateEnum>>().apply {
        value = mutableListOf()
        for (i in SubTypeEnum.values().indices)
            value!!.add(FilterStateEnum.NONE)
        value!![0] = FilterStateEnum.IS
    }
    val subTypeList: LiveData<MutableList<FilterStateEnum>> = mSubTypeList

    private var mCompareCostList = MutableLiveData<MutableList<CompareCard>>().apply {
        value = mutableListOf()
    }
    val compareCostList: LiveData<MutableList<CompareCard>> = mCompareCostList

    private var mComparePowerList = MutableLiveData<MutableList<CompareCard>>().apply {
        value = mutableListOf()
    }
    val comparePowerList: LiveData<MutableList<CompareCard>> = mComparePowerList

    private var mCompareDefenseList = MutableLiveData<MutableList<CompareCard>>().apply {
        value = mutableListOf()
    }
    val compareDefenseList: LiveData<MutableList<CompareCard>> = mCompareDefenseList

    val compareListVisibility = ObservableInt().apply {
        set(View.VISIBLE)
    }

    val compareType = ObservableField<CompareTypeEnum>().apply {
        this.set(CompareTypeEnum.COST)
    }
    val compare = ObservableField<CompareEnum>().apply {
        this.set(CompareEnum.EQUAL)
    }
    val compareValue = ObservableInt()

    private var mRarityList = MutableLiveData<MutableList<FilterStateEnum>>().apply {
        value = mutableListOf()
        for (i in RarityEnum.values().indices)
            value!!.add(FilterStateEnum.NONE)
        value!![0] = FilterStateEnum.IS
    }
    val rarityList: LiveData<MutableList<FilterStateEnum>> = mRarityList
    //End Search Options

    //Begin Search Results
    val masterCardPrintingList = mutableListOf<Printing>()
    val displayCardPrintingList = mutableListOf<Printing>()
    private val mCardPrintingList = MutableLiveData<MutableList<Printing>>().apply {
        value = mutableListOf()
    }
    private val mCardMap = MutableLiveData<MutableMap<String, BaseCard>>().apply {
        value = mutableMapOf()
    }
    private val mPrintingMap = MutableLiveData<MutableMap<String, Printing>>().apply {
        value = mutableMapOf()
    }
    private val mSetMap = MutableLiveData<MutableMap<String, ExpansionSet>>().apply {
        value = mutableMapOf()
    }
    private val mExpansionSetMap = MutableLiveData<MutableMap<ExpansionSet, MutableSet<ExpansionSet>>>().apply {
        value = mutableMapOf()
    }

    val cardPrintingList: LiveData<MutableList<Printing>> = mCardPrintingList
    val cardMap: LiveData<MutableMap<String, BaseCard>> = mCardMap
    val printingMap: LiveData<MutableMap<String, Printing>> = mPrintingMap
    val setMap: LiveData<MutableMap<String, ExpansionSet>> = mSetMap
    val expansionSetMap: LiveData<MutableMap<ExpansionSet, MutableSet<ExpansionSet>>> = mExpansionSetMap

    val cardNameQuery: StringBuilder = StringBuilder()
    var setNameQuery: StringBuilder = StringBuilder()
    //End Search Results

    init {
    }

    fun updateCardPrintingList() {
        if (mCardMap.value!!.isNotEmpty() && mPrintingMap.value!!.isNotEmpty() && mSetMap.value!!.isNotEmpty()) {
            synchronized(masterCardPrintingList) {
                masterCardPrintingList.clear()
                for ((printingId, printing) in mPrintingMap.value!!) {
                    if (setMap.value?.get(printing.setCode) != null) {
                        printing.set = setMap.value?.get(printing.setCode)
                    } else if (setMap.value?.get("ZZZ") != null) {
                        printing.set = setMap.value?.get("ZZZ")
                    }
                    printing.baseCard = mCardMap.value?.get(printing.name.replace(".","")) ?: BaseCard()
                    masterCardPrintingList.add(
                        printing
                    )
                }
                masterCardPrintingList.sortWith(
                    compareBy<Printing> {
                        it.set?.getReleaseDateAsDateToSort()
                    }.thenBy {
                        it.collectorNumber
                    }
                )
                //Re-filter here
                if (cardNameQuery.isNotEmpty())
                    filterCards(cardNameQuery.toString())
                else if (setNameQuery.isNotEmpty())
                    filterCardsBySet(setNameQuery.toString())
            }
        }
    }

    fun updateSets() {
        if (mSetMap.value!!.isNotEmpty()) {
            mExpansionSetMap.value?.clear()
            for ((setCode, expansionSet) in mSetMap.value!!) {
                val parentSet =
                    if (expansionSet.parentSet.isNotEmpty() && mSetMap.value!![expansionSet.parentSet] != null)
                        mSetMap.value!![expansionSet.parentSet]
                    else
                        null

                if (parentSet == null && mExpansionSetMap.value?.get(expansionSet) == null)
                    mExpansionSetMap.value?.set(expansionSet, mutableSetOf())
                else if (parentSet != null)
                    if (mExpansionSetMap.value?.get(parentSet) != null)
                        mExpansionSetMap.value!![parentSet]!!.add(expansionSet)
                    else
                        mExpansionSetMap.value?.set(parentSet, mutableSetOf(expansionSet))
            }
            mExpansionSetMap.notifyObserver()
        }
    }

    fun setTypeFilterStateAtIndex(
        position: Int,
        filterState: FilterStateEnum,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        mTypeList.value!![position] = filterState
        adapter.notifyItemChanged(position)
    }

    fun clearSelectedTypes(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        for ((index, state) in mTypeList.value!!.withIndex()) {
            if (index == 0) {
                mTypeList.value!![0] = FilterStateEnum.IS
                adapter.notifyItemChanged(index)
            } else if (state != FilterStateEnum.NONE) {
                mTypeList.value!![index] = FilterStateEnum.NONE
                adapter.notifyItemChanged(index)
            }
        }
    }

    fun setSubTypeFilterStateAtIndex(
        position: Int,
        filterState: FilterStateEnum,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        mSubTypeList.value!![position] = filterState
        adapter.notifyItemChanged(position)
    }

    fun clearSelectedSubTypes(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        for ((index, state) in mSubTypeList.value!!.withIndex()) {
            if (index == 0) {
                mSubTypeList.value!![0] = FilterStateEnum.IS
                adapter.notifyItemChanged(index)
            } else if (state != FilterStateEnum.NONE) {
                mSubTypeList.value!![index] = FilterStateEnum.NONE
                adapter.notifyItemChanged(index)
            }
        }
    }

    fun setRarityFilterStateAtIndex(
        position: Int,
        filterState: FilterStateEnum,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        mRarityList.value!![position] = filterState
        adapter.notifyItemChanged(position)
    }

    fun clearSelectedRarity(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        for ((index, state) in mRarityList.value!!.withIndex()) {
            if (index == 0) {
                mRarityList.value!![0] = FilterStateEnum.IS
                adapter.notifyItemChanged(index)
            } else if (state != FilterStateEnum.NONE) {
                mRarityList.value!![index] = FilterStateEnum.NONE
                adapter.notifyItemChanged(index)
            }
        }
    }

    fun filterCards(searchText: String) {
        displayCardPrintingList.clear()
        cardNameQuery.clear()

        var distinctRarity = false
        if (mRarityList.value?.get(0) != FilterStateEnum.IS && mRarityList.value?.contains(FilterStateEnum.IS) == true || mRarityList.value!!.contains(
                FilterStateEnum.NOT
            )
        )
            distinctRarity = true

        if (searchText.isNotEmpty())
            cardNameQuery.append(searchText.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT))

        val selectedClass = classSelection.value?.let { classList.value?.get(classSelection.value!!) }

        val selectedPitchList: MutableList<Int> = mutableListOf()
        for ((index, value) in pitchList.value?.withIndex()!!) {
            if (value)
                selectedPitchList.add(index)
        }
        var distinctPrintings = false
        val includedBaseCardVersionMap = mutableMapOf<String, MutableSet<Int>>()
        val includedBaseCardRarityMap = mutableMapOf<String, MutableSet<RarityEnum>>()

        outerLoop@ for (card in masterCardPrintingList) {
            if (!includedBaseCardVersionMap.keys.contains(card.baseCard.name))
                includedBaseCardVersionMap[card.baseCard.name] = mutableSetOf(card.version)
            else if (!includedBaseCardVersionMap[card.baseCard.name]?.add(card.version)!!)
                if (!distinctRarity)
                    continue

            if (distinctRarity)
                if (!passRarityFilter(card, includedBaseCardRarityMap))
                    continue

            if (!filterCardsByName(card, cardNameQuery.toString()))
                continue

            if (!passCardTextFilter(card))
                continue

            if (selectedClass != ClassEnum.ALL)
                if (card.baseCard.getHeroClassAsEnum() != selectedClass && !(includeGenerics.value!! && card.baseCard.getHeroClassAsEnum() == ClassEnum.GENERIC))
                    continue

            if (selectedPitchList.isNotEmpty())
                if (!containsPitch(card, selectedPitchList))
                    continue

            if (mTypeList.value!![0] != FilterStateEnum.IS) {
                if (!mTypeList.value!!.contains(FilterStateEnum.IS)) {
                    if (mTypeList.value!![card.baseCard.getTypeAsEnum().ordinal] == FilterStateEnum.NOT)
                        continue
                } else {
                    if (mTypeList.value!![card.baseCard.getTypeAsEnum().ordinal] != FilterStateEnum.IS)
                        continue
                }
            }

            if (mSubTypeList.value!![0] != FilterStateEnum.IS)
                if (!passSubTypeFilter(card))
                    continue

            if (mCompareCostList.value!!.isNotEmpty())
                if (!passCostFilter(card))
                    continue

            if (mComparePowerList.value!!.isNotEmpty())
                if (!passPowerFilter(card))
                    continue

            if (mCompareDefenseList.value!!.isNotEmpty())
                if (!passDefenseFilter(card))
                    continue

            displayCardPrintingList.add(card)
        }
        mCardPrintingList.value?.clear()
        mCardPrintingList.value?.addAll(displayCardPrintingList)
        mCardPrintingList.value?.sortWith(
            compareBy<Printing> {
                it.baseCard.getHeroClassAsEnum()
            }.thenBy {
                it.baseCard.getTypeAsEnum()
            }.thenBy {
                it.set?.getReleaseDateAsDateToSort()
            }.thenBy {
                it.collectorNumber
            }
        )
        mCardPrintingList.notifyObserver()
    }

    fun filterCards() {
        filterCards(cardNameQuery.toString())
    }

    private fun passCardTextFilter(card: Printing): Boolean {
        for (text in mCardTextList.value!!) {
            if (!card.baseCard.text.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                return false
        }
        return true
    }

    private fun passRarityFilter(
        cardPrinting: Printing,
        includedBaseCardRarityMap: MutableMap<String, MutableSet<RarityEnum>>
    ): Boolean {
        if (!includedBaseCardRarityMap.keys.contains(cardPrinting.baseCard.name))
            includedBaseCardRarityMap[cardPrinting.baseCard.name] = mutableSetOf(cardPrinting.rarity)
        else if (!includedBaseCardRarityMap[cardPrinting.baseCard.name]?.add(cardPrinting.rarity)!!)
            return false

        if (!mRarityList.value!!.contains(FilterStateEnum.IS)) {
            if (mRarityList.value!![cardPrinting.rarity.ordinal] == FilterStateEnum.NOT)
                return false
        } else {
            if (mRarityList.value!![cardPrinting.rarity.ordinal] != FilterStateEnum.IS)
                return false
        }
        return true
    }

    private fun containsPitch(cardPrinting: Printing, selectedPitchList: List<Int>): Boolean {

        if (selectedPitchList.contains(0) && cardPrinting.baseCard.pitch.isEmpty())
            return true
        else if (cardPrinting.baseCard.pitch.isNotEmpty() && selectedPitchList.contains(cardPrinting.baseCard.pitch[cardPrinting.version]))
            return true

        return false
    }

    private fun passSubTypeFilter(cardPrinting: Printing): Boolean {
        var passFilter = false
        if (!mSubTypeList.value!!.contains(FilterStateEnum.IS)) {
            passFilter = true
            if (cardPrinting.baseCard.subTypes.isNotEmpty())
                for (subType in cardPrinting.baseCard.getSubTypesAsEnum()) {
                    if (mSubTypeList.value!![subType.ordinal] == FilterStateEnum.NOT) {
                        passFilter = false
                        break
                    }
                }
        } else
            if (cardPrinting.baseCard.subTypes.isNotEmpty())
                for (subType in cardPrinting.baseCard.getSubTypesAsEnum()) {
                    if (mSubTypeList.value!![subType.ordinal] == FilterStateEnum.IS) {
                        passFilter = true
                        break
                    }
                }

        return passFilter
    }

    private fun passCostFilter(card: Printing): Boolean {
        for (comparison in mCompareCostList.value!!) {
            when (comparison.compare) {
                CompareEnum.EQUAL -> {
                    if (card.baseCard.cost != comparison.value)
                        return false
                }
                CompareEnum.LESS_THAN -> {
                    if (card.baseCard.cost >= comparison.value)
                        return false
                }
                CompareEnum.GREATER_THAN -> {
                    if (card.baseCard.cost <= comparison.value)
                        return false
                }
            }
        }
        return true
    }

    private fun passPowerFilter(card: Printing): Boolean {
        if (card.baseCard.power.isNotEmpty()) {
            val power = card.baseCard.power[card.version]
            for (comparison in mComparePowerList.value!!) {
                when (comparison.compare) {
                    CompareEnum.EQUAL -> {
                        if (power != comparison.value)
                            return false
                    }
                    CompareEnum.LESS_THAN -> {
                        if (power >= comparison.value)
                            return false
                    }
                    CompareEnum.GREATER_THAN -> {
                        if (power <= comparison.value)
                            return false
                    }
                }
            }
            return true
        }
        return false
    }

    private fun passDefenseFilter(card: Printing): Boolean {
        if (card.baseCard.defense.isNotEmpty()) {
            val defense = card.baseCard.defense[card.version]
            for (comparison in mCompareDefenseList.value!!) {
                when (comparison.compare) {
                    CompareEnum.EQUAL -> {
                        if (defense != comparison.value)
                            return false
                    }
                    CompareEnum.LESS_THAN -> {
                        if (defense >= comparison.value)
                            return false
                    }
                    CompareEnum.GREATER_THAN -> {
                        if (defense <= comparison.value)
                            return false
                    }
                }
            }
            return true
        }
        return false
    }

    private fun filterCardsByName(card: Printing, searchText: String): Boolean {
        if (card.baseCard.name.replace("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT).contains(searchText))
            return true
        return false
    }

    fun filterCardsBySet(searchSet: String) {
        displayCardPrintingList.clear()
        setNameQuery.clear()
        setNameQuery.append(searchSet)
        for ((key, printing) in mPrintingMap.value!!) {
            if (printing.set!!.setCode == searchSet)
                displayCardPrintingList.add(
                    printing
                )
        }
        displayCardPrintingList.sortWith(
            compareBy {
                it.collectorNumber
            }
        )
        mCardPrintingList.value?.clear()
        mCardPrintingList.value?.addAll(displayCardPrintingList)
        mCardPrintingList.notifyObserver()
    }

    fun setCardPrintingList() {
        mCardPrintingList.value?.clear()
        mCardPrintingList.value?.addAll(displayCardPrintingList)
        mCardPrintingList.notifyObserver()
    }

    fun addCost(costRecyclerView: RecyclerView) {
        if (!containsComparison(compare.get()!!, compareValue.get(), mCompareCostList.value!!)) {
            mCompareCostList.value?.add(CompareCard(compareType.get()!!, compare.get()!!, compareValue.get()))
//            toggleListVisibility()
            costRecyclerView.adapter?.notifyItemInserted(mCompareCostList.value!!.size - 1)
            mCompareCostList.notifyObserver()
        }
    }

    fun removeCost(position: Int, costRecyclerView: RecyclerView) {
        mCompareCostList.value?.removeAt(position)
//        toggleListVisibility()
        costRecyclerView.adapter?.notifyItemRemoved(position)
        mCompareCostList.notifyObserver()
    }

    fun addPower(powerRecyclerView: RecyclerView) {
        if (!containsComparison(compare.get()!!, compareValue.get(), mComparePowerList.value!!)) {
            mComparePowerList.value?.add(CompareCard(compareType.get()!!, compare.get()!!, compareValue.get()))
//            toggleListVisibility()
            powerRecyclerView.adapter?.notifyItemInserted(mComparePowerList.value!!.size - 1)
            mComparePowerList.notifyObserver()
        }
    }

    fun removePower(position: Int, powerRecyclerView: RecyclerView) {
        mComparePowerList.value?.removeAt(position)
//        toggleListVisibility()
        powerRecyclerView.adapter?.notifyItemRemoved(position)
        mComparePowerList.notifyObserver()
    }

    fun addDefense(defenseRecyclerView: RecyclerView) {
        if (!containsComparison(compare.get()!!, compareValue.get(), mCompareDefenseList.value!!)) {
            mCompareDefenseList.value?.add(CompareCard(compareType.get()!!, compare.get()!!, compareValue.get()))
//            toggleListVisibility()
            defenseRecyclerView.adapter?.notifyItemInserted(mCompareDefenseList.value!!.size - 1)
            mCompareDefenseList.notifyObserver()
        }
    }

    fun removeDefense(position: Int, defenseRecyclerView: RecyclerView) {
        mCompareDefenseList.value?.removeAt(position)
//        toggleListVisibility()
        defenseRecyclerView.adapter?.notifyItemRemoved(position)
        mCompareDefenseList.notifyObserver()
    }

    private fun toggleListVisibility() {
        if (compareListVisibility.get() == View.GONE && (mCompareCostList.value?.isNotEmpty()!! || mComparePowerList.value?.isNotEmpty()!! || mCompareDefenseList.value?.isNotEmpty()!!))
            compareListVisibility.set(View.VISIBLE)
        else if (compareListVisibility.get() == View.VISIBLE && (mCompareCostList.value?.isEmpty()!! && mComparePowerList.value?.isEmpty()!! && mCompareDefenseList.value?.isEmpty()!!))
            compareListVisibility.set(View.GONE)
    }

    private fun containsComparison(compare: CompareEnum, compareValue: Int, compareList: List<CompareCard>): Boolean {
        for (compareCard in compareList) {
            if (compareCard.value == compareValue) {
                if (compareCard.compare == compare)
                    return true
                else continue
            }
        }
        return false
    }

    fun addCardText(text: String, recyclerView: RecyclerView) {
        if (!mCardTextList.value?.contains(text)!!) {
            mCardTextList.value?.add(text)
            recyclerView.adapter?.notifyItemInserted(recyclerView.adapter?.itemCount!!)
            mCardTextList.notifyObserver()
        }
    }

    fun removeCardText(position: Int, recyclerView: RecyclerView) {
        mCardTextList.value?.removeAt(position)
        recyclerView.adapter?.notifyItemRemoved(position)
        mCardTextList.notifyObserver()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

}