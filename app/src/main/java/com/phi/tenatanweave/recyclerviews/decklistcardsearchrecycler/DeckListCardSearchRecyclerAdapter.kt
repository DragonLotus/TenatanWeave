package com.phi.tenatanweave.recyclerviews.decklistcardsearchrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel

class DeckListCardSearchRecyclerAdapter(
    val deckListViewModel: DeckListViewModel,
    val context: Context,
    val increaseOnClickListener: View.OnClickListener,
    val decreaseOnClickListener: View.OnClickListener,
    val heroOnClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<DeckListCardSearchRecyclerViewHolder>() {
    var cardPrintingList: MutableList<Printing> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckListCardSearchRecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.deck_list_detail_linear_row, parent, false)
        return DeckListCardSearchRecyclerViewHolder(view, deckListViewModel)
    }

    override fun onBindViewHolder(holder: DeckListCardSearchRecyclerViewHolder, position: Int) {
        holder.bindCard(
            cardPrintingList[position],
            holder.adapterPosition,
            removeBottomMargin(position),
            increaseOnClickListener,
            decreaseOnClickListener,
            heroOnClickListener,
            context
        )
    }

    fun removeItem(index: Int) {
        if (index < getList().size) {
            cardPrintingList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int {
        return cardPrintingList.size
    }

    fun setList(newList: MutableList<Printing>) {
        cardPrintingList.clear()
        cardPrintingList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getList(): List<Printing> {
        return cardPrintingList
    }

    private fun removeBottomMargin(position: Int): Boolean {
        return position < cardPrintingList.size - 1
    }
}