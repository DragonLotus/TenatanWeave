package com.phi.tenatanweave.recyclerviews.rulingrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CardPrinting
import com.phi.tenatanweave.recyclerviews.cardrecycler.CardRecyclerViewHolder

class RulingRecyclerAdapter(
    var rulingList: MutableList<String>,
    val context: Context
) :
    RecyclerView.Adapter<RulingRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RulingRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.ruling_row, parent, false)
        return RulingRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RulingRecyclerViewHolder, position: Int) {
        holder.bindCard(rulingList[position], context)
    }

    override fun getItemCount(): Int {
        return rulingList.size
    }

    fun getList(): MutableList<String> {
        return rulingList
    }
}