package com.phi.tenatanweave.recyclerviews.rarityrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FilterStateEnum

class RarityRecyclerAdapter(
    val context: Context,
    val rarityList: MutableList<FilterStateEnum>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<RarityRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RarityRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.type_detail_row, parent, false)
        return RarityRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RarityRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, rarityList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return rarityList.size
    }
}