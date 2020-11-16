package com.phi.tenatanweave.recyclerviews.subtyperecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FilterStateEnum

class SubTypeRecyclerAdapter(
    val context: Context,
    val subTypeList: MutableList<FilterStateEnum>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<SubTypeRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTypeRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.type_detail_row, parent, false)
        return SubTypeRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubTypeRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, subTypeList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return subTypeList.size
    }
}