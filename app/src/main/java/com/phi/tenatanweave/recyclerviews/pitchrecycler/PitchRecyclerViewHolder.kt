package com.phi.tenatanweave.recyclerviews.pitchrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.SetEnum
import kotlinx.android.synthetic.main.pitch_row.view.*
import kotlinx.android.synthetic.main.set_detail_row.view.*

class PitchRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSet(context: Context, position: Int, isChecked: Boolean, onClickListener: View.OnClickListener) {
        val resources = context.resources
        val pitchTrueDrawable = resources.getIdentifier("pitch_$position", "drawable", context.packageName)
        val pitchFalseDrawable = resources.getIdentifier("pitch_${position}_grey", "drawable", context.packageName)
        itemView.pitch_true.setImageDrawable(resources.getDrawable(pitchTrueDrawable))
        itemView.pitch_false.setImageDrawable(resources.getDrawable(pitchFalseDrawable))

        when(isChecked){
            true -> {
                itemView.pitch_true.visibility = View.VISIBLE
                itemView.pitch_false.visibility = View.GONE
            }
            false -> {
                itemView.pitch_true.visibility = View.GONE
                itemView.pitch_false.visibility = View.VISIBLE
            }
        }

        itemView.setOnClickListener(onClickListener)
    }
}