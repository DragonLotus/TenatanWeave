package com.phi.tenatanweave.recyclerviews.rulingrecycler

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ruling_row.view.*
import kotlin.reflect.KProperty0

class RulingRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(ruling: String, insertIconsIntoCardText: (String, Int) -> SpannableStringBuilder, context: Context) {
        val textViewHeight = itemView.ruling_text.lineHeight

        with(ruling) {
            itemView.ruling_text.text = insertIconsIntoCardText("\u2022 $ruling", textViewHeight)
        }
    }
}