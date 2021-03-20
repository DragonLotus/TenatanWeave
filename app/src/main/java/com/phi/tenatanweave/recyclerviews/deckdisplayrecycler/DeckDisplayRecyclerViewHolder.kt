package com.phi.tenatanweave.recyclerviews.deckdisplayrecycler

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Deck
import com.phi.tenatanweave.thirdparty.GlideApp
import kotlinx.android.synthetic.main.deck_detail_grid.view.*

class DeckDisplayRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindDeck(deck: Deck, context: Context) {
        with(deck) {
            itemView.deck_name.text = deckName
            itemView.deck_format.text = getFormatAsEnum().toString()

            //Take hero image?
            GlideApp.with(context)
                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/$deckPictureId.png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.horizontal_placeholder)
                .fallback(R.drawable.horizontal_placeholder)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        itemView.deck_image.setImageBitmap(adjustImage(resource, context.resources))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

    private fun adjustImage(cardBitmap: Bitmap, resources: Resources): Bitmap {
        return Bitmap.createBitmap(
            cardBitmap,
            (cardBitmap.width * resources.getFloat(R.dimen.single_card_start_x)).toInt(),
            (cardBitmap.height * resources.getFloat(R.dimen.single_card_start_y)).toInt(),
            (cardBitmap.width * resources.getFloat(R.dimen.single_card_width)).toInt(),
            (cardBitmap.height * resources.getFloat(R.dimen.single_card_height)).toInt()
        )
    }
}