package com.phi.tenatanweave.recyclerviews.setrecycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.ExpansionSet
import com.phi.tenatanweave.recyclerviews.setchildrecyclerview.SetChildRecyclerAdapter

class SetRecyclerAdapter(
    val context: Context,
    val expansionSetMap: MutableMap<ExpansionSet, MutableSet<ExpansionSet>>,
    val parentOnClickListener: View.OnClickListener,
    val childOnClickListener: View.OnClickListener,
    val expandOnClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<SetRecyclerViewHolder>() {

    private var sortedKeyList = expansionSetMap.keys.toList().sortedWith(compareByDescending { it.getReleaseDateAsDateToSort() })
    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetRecyclerViewHolder {
        val linearView: View = LayoutInflater.from(parent.context).inflate(R.layout.set_detail_row, parent, false)
        return SetRecyclerViewHolder(linearView)
    }

    override fun onBindViewHolder(holder: SetRecyclerViewHolder, position: Int) {
        val expansionSet = sortedKeyList[position]
        expansionSetMap[expansionSet]?.let {
            holder.bindSet(
                context,
                expansionSet,
                it,
                position == expandedPosition,
                parentOnClickListener,
                childOnClickListener,
                expandOnClickListener
            )
        }
    }

    override fun getItemCount(): Int {
        return sortedKeyList.size
    }

    fun getList(): List<ExpansionSet> {
        return sortedKeyList
    }

    fun setExpandedPosition(position: Int) {
        val tempPosition = expandedPosition
        val expandable = expansionSetMap[sortedKeyList[position]]!!.size > 0
        if (expandable) {
            if (expandedPosition == position) {
                expandedPosition = -1
                notifyItemChanged(position)
            } else if (expandedPosition >= 0) {
                expandedPosition = position
                notifyItemChanged(tempPosition)
                notifyItemChanged(position)
            } else {
                expandedPosition = position
                notifyItemChanged(position)
            }
        }
    }

    fun notifyDataSetChangedAndUpdate(){
        sortedKeyList = expansionSetMap.keys.toList().sortedWith(compareByDescending { it.getReleaseDateAsDateToSort() })
        notifyDataSetChanged()
    }

}