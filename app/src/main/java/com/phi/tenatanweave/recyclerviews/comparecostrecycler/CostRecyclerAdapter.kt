package com.phi.tenatanweave.recyclerviews.comparecostrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CompareCard

class CostRecyclerAdapter(
    val context: Context,
    val costList: MutableList<CompareCard>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<CostRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.compare_cost_detail_row, parent, false)
        return CostRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CostRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, costList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return costList.size
    }
}