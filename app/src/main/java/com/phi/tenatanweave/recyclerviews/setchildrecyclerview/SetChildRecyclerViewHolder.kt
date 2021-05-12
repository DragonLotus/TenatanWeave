package com.phi.tenatanweave.recyclerviews.setchildrecyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.ExpansionSet
import kotlinx.android.synthetic.main.set_child_detail_row.view.*

class SetChildRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSet(
        context: Context,
        expansionSet: ExpansionSet,
        childOnClickListener: View.OnClickListener
    ) {
        itemView.set_child_name.text = expansionSet.name
        itemView.set_child_card_count.text = context.getString(R.string.cards_in_set_count, expansionSet.cardCount)
        itemView.set_child_release_date.text = expansionSet.getReleaseDateAsDateToDisplay()
        itemView.setOnClickListener(childOnClickListener)

    }
}