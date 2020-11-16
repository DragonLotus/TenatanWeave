package com.phi.tenatanweave.recyclerviews.comparepowerrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CompareCard
import com.phi.tenatanweave.recyclerviews.comparedefenserecycler.CompareDefenseViewHolder

class ComparePowerRecyclerAdapter(
    val context: Context,
    val powerList: MutableList<CompareCard>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<CompareDefenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompareDefenseViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.compare_power_detail_row, parent, false)
        return CompareDefenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompareDefenseViewHolder, position: Int) {
        holder.bindSet(context, position, powerList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return powerList.size
    }
}