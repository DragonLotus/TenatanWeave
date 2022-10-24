package com.phi.tenatanweave.recyclerviews.talentrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FilterStateEnum

class TalentRecyclerAdapter(
    val context: Context,
    val subTypeList: MutableList<FilterStateEnum>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<TalentRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TalentRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.type_detail_row, parent, false)
        return TalentRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TalentRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, subTypeList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return subTypeList.size
    }
}