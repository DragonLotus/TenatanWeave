package com.phi.tenatanweave.recyclerviews.setrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.ExpansionSet
import com.phi.tenatanweave.recyclerviews.setchildrecyclerview.SetChildRecyclerAdapter
import kotlinx.android.synthetic.main.set_child_detail_row.view.*
import kotlinx.android.synthetic.main.set_detail_row.view.*

class SetRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSet(
        context: Context,
        expansionSet: ExpansionSet,
        expansionSetChildren: MutableSet<ExpansionSet>,
        expanded: Boolean,
        parentOnClickListener: View.OnClickListener,
        childOnClickListener: View.OnClickListener,
        expandOnClickListener: View.OnClickListener
    ) {
        itemView.set_name.text = expansionSet.name

        if (expansionSetChildren.isEmpty()) {
            itemView.minimize_arrow.visibility = View.GONE
            itemView.set_card_count.visibility = View.VISIBLE
            itemView.set_release_date.visibility = View.VISIBLE
            itemView.set_card_count.text = context.getString(R.string.cards_in_set_count, expansionSet.cardCount)
            itemView.set_release_date.text = expansionSet.getReleaseDateAsDateToDisplay()
            itemView.setOnClickListener(parentOnClickListener)
        } else {
            itemView.minimize_arrow.visibility = View.VISIBLE
            itemView.set_card_count.visibility = View.GONE
            itemView.set_release_date.visibility = View.GONE
            itemView.setOnClickListener(expandOnClickListener)
        }

        val expansionSetChildRecyclerView = itemView.child_set_recycler_view
        if (expanded) {
            itemView.expand_arrow.visibility = View.VISIBLE
            itemView.minimize_arrow.visibility = View.GONE
            expansionSetChildRecyclerView.visibility = View.VISIBLE
            val expansionSetChildRecyclerAdapter = SetChildRecyclerAdapter(context, expansionSetChildren, childOnClickListener)
            val expansionSetChildLayoutManager = LinearLayoutManager(context)
            expansionSetChildRecyclerView.adapter = expansionSetChildRecyclerAdapter
            expansionSetChildRecyclerView.layoutManager = expansionSetChildLayoutManager
        } else if (expansionSetChildren.isNotEmpty()) {
            itemView.minimize_arrow.visibility = View.VISIBLE
            itemView.expand_arrow.visibility = View.GONE
            expansionSetChildRecyclerView.visibility = View.GONE
        }
    }
}