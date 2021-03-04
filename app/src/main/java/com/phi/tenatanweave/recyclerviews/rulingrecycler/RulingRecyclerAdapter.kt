package com.phi.tenatanweave.recyclerviews.rulingrecycler

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R

class RulingRecyclerAdapter(
    var rulingList: MutableList<String>,
    val insertIconsIntoCardText: (String, Int) -> SpannableStringBuilder,
    val context: Context
) :
    RecyclerView.Adapter<RulingRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RulingRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.ruling_row, parent, false)
        return RulingRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RulingRecyclerViewHolder, position: Int) {
        holder.bindCard(rulingList[position], ::insertIconsIntoCardText.invoke(), context)
    }

    override fun getItemCount(): Int {
        return rulingList.size
    }

    fun getList(): MutableList<String> {
        return rulingList
    }
}