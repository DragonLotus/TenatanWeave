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
            itemView.card_name.text = name

            itemView.card_textview.text = when(getTypeAsEnum()) {
                    TypeEnum.HERO -> "Intellect: $intellect | Health: $health"
                    TypeEnum.EQUIPMENT -> "Defense: ${if(defense.isNotEmpty()) defense[printing.version] else 0}"
                    TypeEnum.WEAPON -> "Power: ${if(power.isNotEmpty()) power[printing.version] else 0}"
                    TypeEnum.ACTION -> "Power: ${if(power.isNotEmpty()) power[printing.version] else 0} | Defense: ${if(defense.isNotEmpty()) defense[printing.version] else 0} | Pitch: ${if(pitch.isNotEmpty()) pitch[printing.version] else 0} | Cost: $cost"
                    TypeEnum.ATTACK_REACTION -> "Power: ${if(power.isNotEmpty()) power[printing.version] else 0} | Defense: ${if(defense.isNotEmpty()) defense[printing.version] else 0} | Pitch: ${if(pitch.isNotEmpty()) pitch[printing.version] else 0} | Cost: $cost"
                    TypeEnum.DEFENSE_REACTION -> "Defense: ${if(defense.isNotEmpty()) defense[printing.version] else 0} | Pitch: ${if(pitch.isNotEmpty()) pitch[printing.version] else 0} | Cost: $cost"
                    TypeEnum.RESOURCE -> "Pitch: ${if(pitch.isNotEmpty()) pitch[printing.version] else 0}"
                    TypeEnum.INSTANT -> "Pitch: ${if(pitch.isNotEmpty()) pitch[printing.version] else 0} | Cost: $cost"
                    TypeEnum.TOKEN -> ""
                    TypeEnum.ALL -> ""
            }

            itemView.card_text2.text = "${getHeroClassAsEnum()} ${getTypeAsEnum()} ${if(subTypes.isNotEmpty()) "- " + getSubTypesAsEnum().joinToString(" ") else ""}"

            GlideApp.with(context)
//                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/" + printing.id + ".png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.card_placeholder)
                .fallback(R.drawable.card_placeholder)
                .into(itemView.card_image)
        }
    }
}