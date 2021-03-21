package com.phi.tenatanweave.recyclerviews.finishrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FinishEnum
import kotlinx.android.synthetic.main.deck_list_detail_linear_row.view.*
import kotlinx.android.synthetic.main.finish_row.view.*
import kotlinx.android.synthetic.main.finish_row.view.finish_image

class FinishRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(finish: FinishEnum, context: Context) {
        itemView.finish_name.text = finish.toString()

        itemView.finish_image.visibility = View.VISIBLE
        when (finish) {
            FinishEnum.RAINBOW -> itemView.finish_image.setImageResource(R.drawable.rainbow_finish)
            FinishEnum.COLD -> itemView.finish_image.setImageResource(R.drawable.cold_finish)
            FinishEnum.GOLD -> itemView.finish_image.setImageResource(R.drawable.gold_finish)
            else -> itemView.finish_image.visibility = View.GONE
        }
    }
}