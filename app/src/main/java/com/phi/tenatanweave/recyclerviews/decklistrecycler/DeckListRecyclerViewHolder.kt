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
import com.phi.tenatanweave.data.enums.FinishEnum
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
        increaseOnClickListener: View.OnClickListener,
        decreaseOnClickListener: View.OnClickListener,
        context: Context
    ) {
        with(printing.cardPrinting) {

            this.baseCard.name.let {
                itemView.deck_list_card_name.text = it
            }

            itemView.deck_list_card_quantity.text =
                deckListViewModel.unsectionedCardPrintingDeckList.count { it.printing.id == this.printing.id && it.finish == this.finish }
                    .toString()

            val scale: Float = context.resources.displayMetrics.density
            val strokeDp = (1.5 * scale + 0.5f).toInt()

            val pitch = this.baseCard.getPitchSafe(this.printing.version)
            var pitchColor = R.color.white
            when (pitch) {
                1 -> pitchColor = R.color.colorRedVersion
                2 -> pitchColor = R.color.colorYellowVersion
                3 -> pitchColor = R.color.colorBlueVersion
            }

            itemView.increase_card_quantity_button.isEnabled = deckListViewModel.checkIfMax(this)
            itemView.decrease_card_quantity_button.isEnabled = itemView.deck_list_card_quantity.text.toString() != "0"
            itemView.increase_card_quantity_button.setOnClickListener(increaseOnClickListener)
            itemView.decrease_card_quantity_button.setOnClickListener(decreaseOnClickListener)

            itemView.finish_image.visibility = View.VISIBLE
            when (this.printing.getFinishSafe(this.finish)) {
                FinishEnum.RAINBOW -> itemView.finish_image.setImageResource(R.drawable.rainbow_finish)
                FinishEnum.COLD -> itemView.finish_image.setImageResource(R.drawable.cold_finish)
                FinishEnum.GOLD -> itemView.finish_image.setImageResource(R.drawable.gold_finish)
                else -> itemView.finish_image.visibility = View.GONE
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
        itemView.increase_card_quantity_button.visibility = View.INVISIBLE
        itemView.decrease_card_quantity_button.visibility = View.INVISIBLE
        itemView.deck_list_card_quantity.visibility = View.INVISIBLE

        with(heroPrinting.cardPrinting) {

            itemView.deck_list_card_name.text =
                if (this?.printing?.name.isNullOrEmpty()) context.getString(R.string.no_hero_selected) else this?.printing?.name

            itemView.finish_image.visibility = View.VISIBLE
            when (this?.finish?.let { this.printing.getFinishSafe(it) }) {
                FinishEnum.RAINBOW -> itemView.finish_image.setImageResource(R.drawable.rainbow_finish)
                FinishEnum.COLD -> itemView.finish_image.setImageResource(R.drawable.cold_finish)
                FinishEnum.GOLD -> itemView.finish_image.setImageResource(R.drawable.gold_finish)
                else -> itemView.finish_image.visibility = View.GONE
            }

            GlideApp.with(context)
                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/${this?.printing?.id}.png")
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