package com.phi.tenatanweave.recyclerviews.rulingrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ruling_row.view.*

class RulingRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(ruling: String, context: Context) {
        with(ruling) {
            itemView.ruling_text.text = "\u2022 $ruling"
        }

    }
}