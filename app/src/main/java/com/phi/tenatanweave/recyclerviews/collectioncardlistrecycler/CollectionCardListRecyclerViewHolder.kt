package com.phi.tenatanweave.recyclerviews.collectioncardlistrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.enums.FinishEnum
import com.phi.tenatanweave.recyclerviews.collectionquantityrecycler.CollectionQuantityRecyclerAdapter
import com.phi.tenatanweave.thirdparty.GlideApp
import com.phi.tenatanweave.thirdparty.glide.CropVerticalCardArt
import kotlinx.android.synthetic.main.collection_card_list_detail_linear_row.view.*

class CollectionCardListRecyclerViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bindCard(
        printing: Printing,
        position: Int,
//        removeBottomMargin: Boolean,
        updateOrAddCollectionEntry : (Int, Printing, FinishEnum) -> Unit,
        context: Context
    ) {
        with(printing) {
            this.baseCard.name.let {
                itemView.collection_list_card_name.text = it
            }

            val scale: Float = context.resources.displayMetrics.density
            val strokeDp = (1.5 * scale + 0.5f).toInt()

            val pitch = this.getPitchSafe()
            var pitchColor = R.color.grey
            when (pitch) {
                1 -> pitchColor = R.color.colorRedVersion
                2 -> pitchColor = R.color.colorYellowVersion
                3 -> pitchColor = R.color.colorBlueVersion
            }
            itemView.collection_list_card_view.strokeColor = context.getColor(pitchColor)
            itemView.collection_list_card_view.strokeWidth = strokeDp

//            if (removeBottomMargin) {
//                val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
//                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, 0)
//            }

            val collectionQuantityRecycler = itemView.collection_quantity_recycler
            val collectionQuantityLayoutManager = GridLayoutManager(context, this.finishList.size)
            val collectionQuantityAdapter = CollectionQuantityRecyclerAdapter(this, updateOrAddCollectionEntry, context)

            collectionQuantityRecycler.layoutManager = collectionQuantityLayoutManager
            collectionQuantityRecycler.adapter = collectionQuantityAdapter

            GlideApp.with(context)
                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/${this.id}.png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.vertical_placeholder)
                .fallback(R.drawable.vertical_placeholder)
                .into(itemView.collection_list_card_image)
        }

    }
}