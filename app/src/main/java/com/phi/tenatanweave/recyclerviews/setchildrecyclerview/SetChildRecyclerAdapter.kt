package com.phi.tenatanweave.recyclerviews.setchildrecyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.ExpansionSet

class SetChildRecyclerAdapter(
    val context: Context,
    val expansionSet: MutableSet<ExpansionSet>,
    val childOnClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<SetChildRecyclerViewHolder>() {

    private val expansionSetListSorted = expansionSet.toList().sortedWith(compareBy { it.releaseDate })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetChildRecyclerViewHolder {
        val linearView: View = LayoutInflater.from(parent.context).inflate(R.layout.set_child_detail_row, parent, false)
        return SetChildRecyclerViewHolder(linearView)
    }

    override fun onBindViewHolder(holder: SetChildRecyclerViewHolder, position: Int) {
        holder.bindSet(context, expansionSetListSorted[position], childOnClickListener)
    }

    override fun getItemCount(): Int {
        return expansionSetListSorted.size
    }

    fun getList(): List<ExpansionSet> {
        return expansionSetListSorted
    }
}