package com.phi.tenatanweave.recyclerviews.cardrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Printing

class CardRecyclerAdapter(
    var cardPrintingList: MutableList<Printing>,
    val context: Context,
    var grid: Boolean,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<CardRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardRecyclerViewHolder {
        val view: View = if (grid)
            LayoutInflater.from(parent.context).inflate(R.layout.card_detail_grid, parent, false)
        else
            LayoutInflater.from(parent.context).inflate(R.layout.card_detail_linear_row, parent, false)
        view.setOnClickListener(onClickListener)
        return CardRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardRecyclerViewHolder, position: Int) {
        holder.bindCard(cardPrintingList[position].baseCard, cardPrintingList[position], context)
    }

    override fun getItemCount(): Int {
        return cardPrintingList.size
    }

    fun getList(): MutableList<Printing> {
        return cardPrintingList
    }
}