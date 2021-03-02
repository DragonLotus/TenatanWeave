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

    fun setDeck(deck: Deck) {
        mDeck.value = deck
        mDeck.notifyObserver()
    }

    fun processDeck(
        printingMap: MutableMap<String, Printing>,
        cardMap: MutableMap<String, BaseCard>,
        context: Context
    ) {
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
                if(baseCard?.pitch.isNullOrEmpty()){
                    printing?.let { RecyclerItem.Printing(it) }?.let { printingMiscList.add(it) }
                } else {
                    when (printing?.version?.let { baseCard?.pitch?.get(it) }) {
                        1 -> printingRedList.add(RecyclerItem.Printing(printing))
                        2 -> printingYellowList.add(RecyclerItem.Printing(printing))
                        3 -> printingBlueList.add(RecyclerItem.Printing(printing))
                    }
                }
            }
        }

        printingRedList.sortWith(compareBy { it.printing.name })
        printingYellowList.sortWith(compareBy { it.printing.name })
        printingBlueList.sortWith(compareBy { it.printing.name })
        printingMiscList.sortWith(compareBy { it.printing.name })

        val printingList = mutableListOf<RecyclerItem>()
        if(printingMiscList.isNotEmpty()){
            printingList.addAll(
                listOf(
                    RecyclerItem.SetSection(
                        context.getString(
                            R.string.label_pitch_misc,
                            printingMiscList.size
                        )
                    )
                ) + printingMiscList
            )
        }

        printingList.addAll(
            listOf(
                RecyclerItem.SetSection(
                    context.getString(
                        R.string.label_pitch_red,
                        printingRedList.size
                    )
                )
            ) + printingRedList
        )

        printingList.addAll(
            listOf(
                RecyclerItem.SetSection(
                    context.getString(
                        R.string.label_pitch_yellow,
                        printingYellowList.size
                    )
                )
            ) + printingYellowList
        )

        printingList.addAll(
            listOf(
                RecyclerItem.SetSection(
                    context.getString(
                        R.string.label_pitch_blue,
                        printingBlueList.size
                    )
                )
            ) + printingBlueList
        )

        return printingList
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
