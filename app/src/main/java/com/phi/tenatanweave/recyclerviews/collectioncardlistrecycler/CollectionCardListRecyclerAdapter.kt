package com.phi.tenatanweave.recyclerviews.collectioncardlistrecycler

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CollectionEntry
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.enums.FinishEnum

class CollectionCardListRecyclerAdapter(
    val context: Context,
    val currentSetCollectionEntryMap: MutableMap<String, CollectionEntry>,
    val updateOrAddCollectionEntry : (Int, Printing, FinishEnum, Resources) -> Unit
) :
    RecyclerView.Adapter<CollectionCardListRecyclerViewHolder>() {
    var printingsList: MutableList<Printing> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionCardListRecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.collection_card_list_detail_linear_row, parent, false)
        return CollectionCardListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionCardListRecyclerViewHolder, position: Int) {
        holder.bindCard(printingsList[position], currentSetCollectionEntryMap, position, updateOrAddCollectionEntry, context)
    }

    override fun getItemCount(): Int {
        return printingsList.size
    }

//    override fun getItemViewType(position: Int) = when (printingsList[position]) {
//        is RecyclerItem.SetSection -> TYPE_SETSECTION
//        is RecyclerItem.Printing -> TYPE_PRINTING
//        is RecyclerItem.HeroPrinting -> TYPE_HERO
//    }

    fun removeItem(index: Int) {
        if (index < getList().size) {
            printingsList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun setList(newList: MutableList<Printing>) {
        printingsList.clear()
        printingsList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getList(): List<Printing> {
        return printingsList
    }

//    private fun removeBottomMargin(position: Int): Boolean {
//        return if (position == printingsList.size - 1)
//            true
//        else
//            when (printingsList[position + 1]) {
//                is RecyclerItem.Printing -> true
//                is RecyclerItem.SetSection -> false
//                is RecyclerItem.HeroPrinting -> false
//            }
//
//    }
}