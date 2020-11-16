package com.phi.tenatanweave.recyclerviews.finishrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.data.enums.FinishEnum
import kotlinx.android.synthetic.main.finish_row.view.*

class FinishRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(finish: FinishEnum, context: Context) {
        itemView.finish_name.text = finish.toString()

    }
}