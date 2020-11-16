package com.phi.tenatanweave.recyclerviews.comparecostrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CompareCard
import kotlinx.android.synthetic.main.compare_cost_detail_row.view.*

class CostRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSet(context: Context, position: Int, compareCard: CompareCard, onClickListener: View.OnClickListener) {
        val resources = context.resources
        itemView.cost_chip.apply {
            text = resources.getString(R.string.cost_chip_text, compareCard.compare.toString(), compareCard.value.toString())
            setOnCloseIconClickListener(onClickListener)
        }
    }
}