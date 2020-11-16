package com.phi.tenatanweave.recyclerviews.printingsrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.RecyclerItem
import com.phi.tenatanweave.recyclerviews.finishrecycler.FinishRecyclerAdapter
import com.phi.tenatanweave.thirdparty.GlideApp
import kotlinx.android.synthetic.main.printing_row.view.*
import kotlinx.android.synthetic.main.section_row.view.*

class PrintingsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(printing: RecyclerItem.Printing, context: Context) {
        with(printing.printing) {
            GlideApp.with(context)
//                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/" + this.id + ".png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.card_placeholder)
                .fallback(R.drawable.card_placeholder)
                .into(itemView.card_image)

            val finishRecycler = itemView.finish_recycler
            val finishRecyclerAdapter = FinishRecyclerAdapter(this.finish, context)
            val finishRecyclerLayoutManager = GridLayoutManager(context, this.finish.size)
            finishRecycler.adapter = finishRecyclerAdapter
            finishRecycler.layoutManager = finishRecyclerLayoutManager
            finishRecycler.suppressLayout(true)

        }

    }

    fun bindSection(setSection: RecyclerItem.SetSection, context: Context) {
        itemView.set_section_name.text = setSection.setName.toString()
    }
}