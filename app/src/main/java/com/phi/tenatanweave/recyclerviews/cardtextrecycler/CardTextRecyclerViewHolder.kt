package com.phi.tenatanweave.recyclerviews.cardtextrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FilterStateEnum
import com.phi.tenatanweave.data.enums.SubTypeEnum
import kotlinx.android.synthetic.main.type_detail_row.view.*

class CardTextRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSet(context: Context, position: Int, cardText: String, onClickListener: View.OnClickListener) {
        val resources = context.resources
        itemView.type_chip.apply {
            text = cardText
            setChipBackgroundColorResource(R.color.colorPrimary)
            setOnCloseIconClickListener(onClickListener)
        }
    }
}