package com.phi.tenatanweave.recyclerviews.comparedefenserecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CompareCard

class CompareDefenseRecyclerAdapter(
    val context: Context,
    val defenseList: MutableList<CompareCard>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<CompareDefenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompareDefenseViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.compare_defense_detail_row, parent, false)
        return CompareDefenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompareDefenseViewHolder, position: Int) {
        holder.bindSet(context, position, defenseList[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return defenseList.size
    }
}