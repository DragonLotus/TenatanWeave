package com.phi.tenatanweave.recyclerviews.finishrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FinishEnum

class FinishRecyclerAdapter(
    var finishList: List<FinishEnum>,
    val context: Context
) :
    RecyclerView.Adapter<FinishRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.finish_row, parent, false)
        return FinishRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinishRecyclerViewHolder, position: Int) {
        holder.bindCard(finishList[position], context)
    }

    override fun getItemCount(): Int {
        return finishList.size
    }

    fun getList(): List<FinishEnum> {
        return finishList
    }
}