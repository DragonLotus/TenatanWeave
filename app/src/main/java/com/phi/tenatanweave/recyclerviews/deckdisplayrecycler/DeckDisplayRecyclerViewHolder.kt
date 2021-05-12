package com.phi.tenatanweave.recyclerviews.deckdisplayrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Deck
import com.phi.tenatanweave.thirdparty.GlideApp
import com.phi.tenatanweave.thirdparty.glide.CropVerticalCardArt
import kotlinx.android.synthetic.main.deck_detail_grid.view.*

class DeckDisplayRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindDeck(deck: Deck, context: Context) {
        with(deck) {
            itemView.deck_name.text = deckName
            itemView.deck_format.text = format

            //Take hero image?
            GlideApp.with(context)
                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/$deckPictureId.png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CropVerticalCardArt())
                .placeholder(R.drawable.horizontal_placeholder)
                .fallback(R.drawable.horizontal_placeholder)
                .into(itemView.deck_image)
        }
    }
}