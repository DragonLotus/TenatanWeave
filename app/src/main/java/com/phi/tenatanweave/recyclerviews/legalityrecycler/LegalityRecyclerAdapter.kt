package com.phi.tenatanweave.recyclerviews.legalityrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FormatEnum

class LegalityRecyclerAdapter(
    var formatList: MutableList<FormatEnum>,
    var legalFormatList: MutableList<FormatEnum>,
    val context: Context
) :
    RecyclerView.Adapter<LegalityRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegalityRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.legality_row, parent, false)
        return LegalityRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: LegalityRecyclerViewHolder, position: Int) {
        holder.bindCard(formatList[position], legalFormatList, context)
    }

    override fun getItemCount(): Int {
        return formatList.size
    }

    fun getList(): MutableList<FormatEnum> {
        return formatList
    }
}