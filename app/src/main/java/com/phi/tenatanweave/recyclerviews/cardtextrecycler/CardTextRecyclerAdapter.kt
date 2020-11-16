package com.phi.tenatanweave.recyclerviews.cardtextrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R

class CardTextRecyclerAdapter(
    val context: Context,
    val cardTextList: MutableList<String>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<CardTextRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTextRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_text_row, parent, false)
        return CardTextRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardTextRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, cardTextList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return cardTextList.size
    }
}