package com.phi.tenatanweave.recyclerviews.cardrecycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.BaseCard
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.enums.TypeEnum
import com.phi.tenatanweave.thirdparty.GlideApp
import kotlinx.android.synthetic.main.card_detail_linear_row.view.*

class CardRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(card: BaseCard, printing: Printing, context: Context) {
        with(card) {
            itemView.deck_list_card_name.text = name

            itemView.card_textview.text = when (getTypeAsEnum()) {
                TypeEnum.HERO -> "Intellect: $intellect | Health: $health"
                TypeEnum.EQUIPMENT -> "Defense: ${getDefenseSafe(printing.version)}"
                TypeEnum.WEAPON -> "Power: ${getPowerSafe(printing.version)}"
                TypeEnum.ACTION -> "Power: ${getPowerSafe(printing.version)} | Defense: ${getDefenseSafe(printing.version)} | Pitch: ${
                    getPitchSafe(
                        printing.version
                    )
                } | Cost: $cost"
                TypeEnum.ATTACK_REACTION -> "Power: ${getPowerSafe(printing.version)} | Defense: ${
                    getDefenseSafe(
                        printing.version
                    )
                } | Pitch: ${getPitchSafe(printing.version)} | Cost: $cost"
                TypeEnum.DEFENSE_REACTION -> "Defense: ${getDefenseSafe(printing.version)} | Pitch: ${
                    getPitchSafe(
                        printing.version
                    )
                } | Cost: $cost"
                TypeEnum.RESOURCE -> "Pitch: ${getPitchSafe(printing.version)}"
                TypeEnum.INSTANT -> "Pitch: ${getPitchSafe(printing.version)} | Cost: $cost"
                TypeEnum.TOKEN -> ""
                TypeEnum.MENTOR -> ""
                TypeEnum.ALL -> ""
            }

            itemView.card_text2.text = if (getTypeAsEnum() != TypeEnum.ALL) {
                "${getHeroClassAsEnum()} ${getTypeAsEnum()} ${
                    if (subTypes.isNotEmpty()) "- " + getSubTypesAsEnum().joinToString(
                        " "
                    ) else ""
                }"
            } else {
                "${getHeroClassAsEnum()} ${if (subTypes.isNotEmpty()) "- " + getSubTypesAsEnum().joinToString(" ") else ""}"
            }

            GlideApp.with(context)
//                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/" + printing.id + ".png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.vertical_placeholder)
                .fallback(R.drawable.vertical_placeholder)
                .into(itemView.deck_list_card_image)
        }
    }
}