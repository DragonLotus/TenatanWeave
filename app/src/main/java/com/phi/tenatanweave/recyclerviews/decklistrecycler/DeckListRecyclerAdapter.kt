package com.phi.tenatanweave.recyclerviews.decklistrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.RecyclerItem
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel

class DeckListRecyclerAdapter(
    val deckListViewModel: DeckListViewModel,
    val context: Context
) :
    RecyclerView.Adapter<DeckListRecyclerViewHolder>() {
    var printingsList: MutableList<RecyclerItem> = mutableListOf()
    private val TYPE_SETSECTION = 0
    private val TYPE_PRINTING = 1
    private val TYPE_HERO = 2
    private val TYPE_CARD_PRINTING = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_SETSECTION -> DeckListRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.section_row, parent, false), deckListViewModel
        )
        TYPE_PRINTING -> DeckListRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.deck_list_detail_linear_row, parent, false), deckListViewModel
        )
        TYPE_HERO -> DeckListRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.deck_list_detail_linear_row, parent, false), deckListViewModel
        )
        TYPE_CARD_PRINTING -> DeckListRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.deck_list_detail_linear_row, parent, false), deckListViewModel
        )
        else -> DeckListRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.section_row, parent, false), deckListViewModel
        )
    }

    override fun onBindViewHolder(holder: DeckListRecyclerViewHolder, position: Int) {
        when (val item = printingsList[holder.adapterPosition]) {
            is RecyclerItem.CardPrinting -> holder.bindCard(item, holder.adapterPosition, removeBottomMargin(position), context)
            is RecyclerItem.SetSection -> holder.bindSection(item, context)
            is RecyclerItem.HeroPrinting -> holder.bindHero(item, context)
        }
    }

    override fun getItemCount(): Int {
        return printingsList.size
    }

    override fun getItemViewType(position: Int) = when (printingsList[position]) {
        is RecyclerItem.SetSection -> TYPE_SETSECTION
        is RecyclerItem.Printing -> TYPE_PRINTING
        is RecyclerItem.HeroPrinting -> TYPE_HERO
        is RecyclerItem.CardPrinting -> TYPE_CARD_PRINTING
    }

    fun setList(newList: MutableList<RecyclerItem>) {
        printingsList.clear()
        printingsList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getList(): List<RecyclerItem> {
        return printingsList
    }

    private fun removeBottomMargin(position: Int): Boolean {
        return if (position == printingsList.size - 1)
            true
        else
            when (printingsList[position + 1]) {
                is RecyclerItem.Printing -> true
                is RecyclerItem.SetSection -> false
                is RecyclerItem.HeroPrinting -> false
                is RecyclerItem.CardPrinting -> false
            }

    }
}