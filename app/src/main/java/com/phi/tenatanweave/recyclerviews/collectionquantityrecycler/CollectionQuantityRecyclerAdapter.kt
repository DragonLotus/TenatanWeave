package com.phi.tenatanweave.recyclerviews.collectionquantityrecycler

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

class CollectionQuantityRecyclerAdapter(
    var printing: Printing,
    val currentSetCollectionEntryMap: MutableMap<String, CollectionEntry>,
    val updateOrAddCollectionEntry : (Int, Printing, FinishEnum, Resources) -> Unit,
    val context: Context
) :
    RecyclerView.Adapter<CollectionQuantityRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionQuantityRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.collection_quantity_detail_grid_row, parent, false)
        return CollectionQuantityRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionQuantityRecyclerViewHolder, position: Int) {
        holder.bindCard(printing.finishList[position], currentSetCollectionEntryMap, printing, updateOrAddCollectionEntry, context)
    }

    override fun getItemCount(): Int {
        return printing.finishList.size
    }

    fun getList(): List<FinishEnum> {
        return printing.finishList
    }
}