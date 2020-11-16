package com.phi.tenatanweave.recyclerviews.pitchrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R

class PitchRecyclerAdapter(
    val context: Context,
    var pitchList: MutableList<Boolean>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<PitchRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PitchRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.pitch_row, parent, false)
        return PitchRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PitchRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, pitchList[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return pitchList.size
    }
}