package com.phi.tenatanweave.recyclerviews.decklistrecycler

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.RecyclerItem
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel
import com.phi.tenatanweave.thirdparty.GlideApp
import kotlinx.android.synthetic.main.deck_list_detail_linear_row.view.*
import kotlinx.android.synthetic.main.section_row.view.*

class DeckListRecyclerViewHolder(itemView: View, private val deckListViewModel: DeckListViewModel) :
    RecyclerView.ViewHolder(itemView) {

    fun bindCard(
        printing: RecyclerItem.CardPrinting,
        position: Int,
        removeBottomMargin: Boolean,
        context: Context
    ) {
        with(printing.cardPrinting) {

            this.baseCard.name.let {
                itemView.deck_list_card_name.text = it
            }

            if (deckListViewModel.deckQuantityMap.value?.get(this.printing.id) != null)
                itemView.deck_list_card_quantity.text = deckListViewModel.deckQuantityMap.value?.get(this.printing.id).toString()
            else
                itemView.deck_list_card_quantity.text = "0"

            val scale: Float = context.resources.displayMetrics.density
            val strokeDp = (1.5 * scale + 0.5f).toInt()

            val pitch = if(this.baseCard.pitch.isEmpty()) -1 else this.baseCard.pitch[this.printing.version]
            var pitchColor = R.color.white
            if (pitch != null)
                when (pitch) {
                    1 -> pitchColor = R.color.colorRedVersion
                    2 -> pitchColor = R.color.colorYellowVersion
                    3 -> pitchColor = R.color.colorBlueVersion
                }


            itemView.deck_list_card_view.strokeColor = context.getColor(pitchColor)
            itemView.deck_list_card_view.strokeWidth = strokeDp

            if (removeBottomMargin) {
                val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, 0)
            }

            GlideApp.with(context)
                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/${this.printing.id}.png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.horizontal_placeholder)
                .fallback(R.drawable.horizontal_placeholder)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        itemView.deck_list_card_image.setImageBitmap(adjustImage(resource, context.resources))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
//                        TODO("Not yet implemented")
                    }

                })
        }

    }

    fun bindSection(setSection: RecyclerItem.SetSection, context: Context) {
        itemView.set_section_name.text = setSection.setName
    }

    fun bindHero(heroPrinting: RecyclerItem.HeroPrinting, context: Context) {
        itemView.deck_list_card_name.text = context.getString(R.string.no_hero_selected)
        itemView.increase_card_quantity_button.visibility = View.INVISIBLE
        itemView.decrease_card_quantity_button.visibility = View.INVISIBLE
        itemView.deck_list_card_quantity.visibility = View.INVISIBLE

        if (heroPrinting.printing != null) {
            with(heroPrinting.printing) {

                itemView.deck_list_card_name.text = heroPrinting.printing.name

                GlideApp.with(context)
                    .asBitmap()
                    .load(
                        Firebase.storage.reference
                            .child("card_images/${this.id}.png")
                    )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.horizontal_placeholder)
                    .fallback(R.drawable.horizontal_placeholder)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            itemView.deck_list_card_image.setImageBitmap(adjustImage(resource, context.resources))
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("Not yet implemented")
                        }

                    })
            }
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