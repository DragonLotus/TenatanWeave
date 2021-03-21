package com.phi.tenatanweave.recyclerviews.decklistcardsearchrecycler

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
import com.phi.tenatanweave.data.CardPrinting
import com.phi.tenatanweave.data.enums.FinishEnum
import com.phi.tenatanweave.data.enums.SubTypeEnum
import com.phi.tenatanweave.data.enums.TypeEnum
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel
import com.phi.tenatanweave.thirdparty.GlideApp
import kotlinx.android.synthetic.main.deck_list_detail_linear_row.view.*

class DeckListCardSearchRecyclerViewHolder(itemView: View, private val deckListViewModel: DeckListViewModel) :
    RecyclerView.ViewHolder(itemView) {

    fun bindCard(
        cardPrinting: CardPrinting,
        position: Int,
        removeBottomMargin: Boolean,
        increaseOnClickListener: View.OnClickListener,
        decreaseOnClickListener: View.OnClickListener,
        heroOnClickListener: View.OnClickListener,
        context: Context
    ) {
        with(cardPrinting) {

            this.printing.name.let {
                itemView.deck_list_card_name.text = it
            }

            itemView.deck_list_card_quantity.text =
                deckListViewModel.unsectionedCardPrintingDeckList.count { it.printing.id == this.printing.id && it.finish == this.finish }
                    .toString()

            itemView.deck_list_card_type.text =
                "${this.baseCard.getHeroClassAsEnum()} ${this.baseCard.getTypeAsEnum().toFullString()} ${
                    if (this.baseCard.subTypes.isNotEmpty()) "- " + this.baseCard.getSubTypesAsEnum().joinToString(" ")
                        .replace(SubTypeEnum.ALL.toString(), "NA") else ""
                }"

            if (this.baseCard.cost >= 0) {
                itemView.cost_layout.visibility = View.VISIBLE
                itemView.cost_textview.text = this.baseCard.cost.toString()
            } else
                itemView.cost_layout.visibility = View.GONE

            if (this.baseCard.power.isNotEmpty()) {
                itemView.power_layout.visibility = View.VISIBLE
                itemView.power_textview.text = this.baseCard.getPowerSafe(this.printing.version).toString()
            } else
                itemView.power_layout.visibility = View.GONE

            if (this.baseCard.defense.isNotEmpty()) {
                itemView.defense_layout.visibility = View.VISIBLE
                itemView.defense_textview.text = this.baseCard.getDefenseSafe(this.printing.version).toString()
            } else
                itemView.defense_layout.visibility = View.GONE

            if (this.baseCard.intellect >= 0) {
                itemView.intelligence_layout.visibility = View.VISIBLE
                itemView.intelligence_textview.text = this.baseCard.intellect.toString()
            } else
                itemView.intelligence_layout.visibility = View.GONE

            if (this.baseCard.health >= 0) {
                itemView.health_layout.visibility = View.VISIBLE
                itemView.health_textview.text = this.baseCard.health.toString()
            } else
                itemView.health_layout.visibility = View.GONE

            val scale: Float = context.resources.displayMetrics.density
            val strokeDp = (1.5 * scale + 0.5f).toInt()

            val pitch = this.baseCard.getPitchSafe(this.printing.version)
            var pitchColor = R.color.grey
            when (pitch) {
                1 -> pitchColor = R.color.colorRedVersion
                2 -> pitchColor = R.color.colorYellowVersion
                3 -> pitchColor = R.color.colorBlueVersion
            }

            if(this.baseCard.getTypeAsEnum() != TypeEnum.HERO){
                itemView.increase_card_quantity_button.isEnabled = deckListViewModel.checkIfNotMax(this)
                itemView.decrease_card_quantity_button.isEnabled = itemView.deck_list_card_quantity.text.toString() != "0"
                itemView.increase_card_quantity_button.setOnClickListener (increaseOnClickListener)
                itemView.decrease_card_quantity_button.setOnClickListener (decreaseOnClickListener)
                itemView.increase_card_quantity_button.visibility = View.VISIBLE
                itemView.decrease_card_quantity_button.visibility = View.VISIBLE
                itemView.deck_list_card_quantity.visibility = View.VISIBLE
            } else {
                itemView.setOnClickListener(heroOnClickListener)
                itemView.increase_card_quantity_button.visibility = View.GONE
                itemView.decrease_card_quantity_button.visibility = View.GONE
                itemView.deck_list_card_quantity.visibility = View.GONE
            }

            itemView.finish_image.visibility = View.VISIBLE
            when (this.finish.let { this.printing.getFinishSafe(it) }) {
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