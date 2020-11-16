package com.phi.tenatanweave.recyclerviews.legalityrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FormatEnum
import kotlinx.android.synthetic.main.legality_row.view.*

class LegalityRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(format: FormatEnum, legalFormatList: MutableList<FormatEnum>, context: Context) {
        with(format) {
            itemView.format_name.text = this.toString()

            if(legalFormatList.contains(format)){
                itemView.legality_chip.text = context.getString(R.string.label_legal)
                itemView.legality_chip.setChipBackgroundColorResource(R.color.colorIs)
            } else {
                itemView.legality_chip.text = context.getString(R.string.label_not_legal)
                itemView.legality_chip.setChipBackgroundColorResource(R.color.colorNot)
            }
        }

    }
}