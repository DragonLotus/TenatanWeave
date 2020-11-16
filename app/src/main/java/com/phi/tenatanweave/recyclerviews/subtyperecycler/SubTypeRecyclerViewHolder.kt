package com.phi.tenatanweave.recyclerviews.subtyperecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FilterStateEnum
import com.phi.tenatanweave.data.enums.SubTypeEnum
import kotlinx.android.synthetic.main.type_detail_row.view.*

class SubTypeRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSet(context: Context, position: Int, filterState: FilterStateEnum, onClickListener: View.OnClickListener) {
        val resources = context.resources
        itemView.type_chip.apply {
            text = SubTypeEnum.values()[position].toString()
            when (filterState) {
                FilterStateEnum.NONE -> {
                    chipIcon = null
                    setChipBackgroundColorResource(R.color.colorPrimary)
                }
                FilterStateEnum.IS -> {
                    chipIcon = resources.getDrawable(R.drawable.ic_chip_checked_circle)
                    setChipBackgroundColorResource(R.color.colorIs)
                }
                FilterStateEnum.NOT -> {
                    chipIcon = resources.getDrawable(R.drawable.ic_chip_close_circle)
                    setChipBackgroundColorResource(R.color.colorNot)
                }
            }
            setOnClickListener(onClickListener)
        }
    }
}