package com.phi.tenatanweave.recyclerviews.deckdisplayrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CardPrinting
import com.phi.tenatanweave.data.Deck

class DeckDisplayRecyclerAdapter(
    val context: Context,
    val onClickListener: View.OnClickListener,
    val onLongClickListener: View.OnLongClickListener
) :
    RecyclerView.Adapter<DeckDisplayRecyclerViewHolder>() {
    private val deckList: MutableList<Deck> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckDisplayRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.deck_detail_grid, parent, false)
        view.setOnClickListener(onClickListener)
        view.setOnLongClickListener(onLongClickListener)
        return DeckDisplayRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckDisplayRecyclerViewHolder, position: Int) {
        holder.bindDeck(deckList[position], context)
    }

    override fun getItemCount(): Int {
        return deckList.size
    }

    fun getList(): MutableList<Deck> {
        return deckList
    }

    fun setList(newList : MutableList<Deck>) {
        deckList.clear()
        deckList.addAll(newList)
        notifyDataSetChanged()
    }
}