package com.phi.tenatanweave.recyclerviews.printingsrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.RecyclerItem

class PrintingsRecyclerAdapter(
    var printingsList: List<RecyclerItem>,
    val context: Context
) :
    RecyclerView.Adapter<PrintingsRecyclerViewHolder>() {
    private val TYPE_SETSECTION = 0
    private val TYPE_PRINTING = 1
    private val TYPE_HERO = 2
    private val TYPE_CARD_PRINTING = 3

    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int) = when(viewType) {
        TYPE_SETSECTION -> PrintingsRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.section_row, parent, false))
        TYPE_PRINTING -> PrintingsRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.printing_row, parent, false))
        else -> PrintingsRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.section_row, parent, false))
    }


    override fun onBindViewHolder(holder: PrintingsRecyclerViewHolder, position: Int) {
        when(val item = printingsList[holder.adapterPosition]){
            is RecyclerItem.Printing -> holder.bindCard(item, context)
            is RecyclerItem.SetSection -> holder.bindSection(item, context)
        }
    }

    override fun getItemCount(): Int {
        return printingsList.size
    }

    override fun getItemViewType(position: Int) = when(printingsList[position]){
        is RecyclerItem.SetSection -> TYPE_SETSECTION
        is RecyclerItem.Printing -> TYPE_PRINTING
        is RecyclerItem.HeroPrinting -> TYPE_HERO
        is RecyclerItem.CardPrinting -> TYPE_CARD_PRINTING
    }

    fun getList(): List<RecyclerItem> {
        return printingsList
    }
}