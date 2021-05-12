package com.phi.tenatanweave.fragments.singlecard

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.enums.SubTypeEnum
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.legalityrecycler.LegalityRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.printingsrecycler.PrintingsRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.rulingrecycler.RulingRecyclerAdapter
import com.phi.tenatanweave.thirdparty.GlideApp
import com.phi.tenatanweave.thirdparty.glide.CropVerticalCardArt
import kotlinx.android.synthetic.main.deck_list_detail_linear_row.view.*


class SingleCardFragment : Fragment() {

    private val singleCardViewModel: SingleCardViewModel by activityViewModels()
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()
    private val deckListViewModel: DeckListViewModel by activityViewModels()
    private val args: SingleCardFragmentArgs by navArgs()

    private lateinit var powerDrawable: Drawable
    private lateinit var defenseDrawable: Drawable
    private lateinit var healthDrawable: Drawable
    private lateinit var intelligenceDrawable: Drawable
    private lateinit var resourceDrawable: Drawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_singlecard, container, false)
        val singleCardConstraintLayout = root.findViewById<ConstraintLayout>(R.id.single_card_constraint_layout)

        singleCardViewModel.setCurrentPosition(args.clickedPosition)
        //On Resume list is empty
        searchCardResultViewModel.cardPrintingList.value?.get(args.clickedPosition)
            ?.let {
                singleCardViewModel.setCardPrinting(
                    it,
                    searchCardResultViewModel.printingMap.value!!,
                    searchCardResultViewModel.cardMap.value!!,
                    searchCardResultViewModel.setMap.value!!,
                    false
                )
            }

        val toolbar: Toolbar = root.findViewById(R.id.toolbar)

        powerDrawable = requireContext().getDrawable(R.drawable.ic_power)!!
        defenseDrawable = requireContext().getDrawable(R.drawable.ic_defense)!!
        healthDrawable = requireContext().getDrawable(R.drawable.ic_health)!!
        intelligenceDrawable = requireContext().getDrawable(R.drawable.ic_intelligence)!!
        resourceDrawable = requireContext().getDrawable(R.drawable.ic_resource)!!

        val cardImage = root.findViewById<ImageView>(R.id.deck_list_card_image)
//        val resourceLayout = root.findViewById<ConstraintLayout>(R.id.resource_layout)
//        val heroStatsLayout = root.findViewById<ConstraintLayout>(R.id.hero_stats_layout)
        val pitchImageView = root.findViewById<ImageView>(R.id.pitch_imageview)
        val costTextView = root.findViewById<TextView>(R.id.cost_textview)
        val costImageView = root.findViewById<ImageView>(R.id.cost_imageview)
        val intelligenceTextView = root.findViewById<TextView>(R.id.intelligence_textview)
        val intelligenceImageView = root.findViewById<ImageView>(R.id.intelligence_imageview)
        val healthTextView = root.findViewById<TextView>(R.id.health_textview)
        val healthImageView = root.findViewById<ImageView>(R.id.health_imageview)
        val powerTextView = root.findViewById<TextView>(R.id.power_textview)
        val powerImageView = root.findViewById<ImageView>(R.id.power_imageview)
        val defenseTextView = root.findViewById<TextView>(R.id.defense_textview)
        val defenseImageView = root.findViewById<ImageView>(R.id.defense_imageview)
        val cardTypeTextView = root.findViewById<TextView>(R.id.card_type_textview)
        val rarityTextView = root.findViewById<TextView>(R.id.rarity_textview)
        val versionsChipGroup = root.findViewById<ChipGroup>(R.id.versions_chip_group)
        val heroVersionsChipGroup = root.findViewById<ChipGroup>(R.id.hero_versions_chip_group)
        val adultVersionChip = root.findViewById<Chip>(R.id.adult_version_chip)
        val youngVersionChip = root.findViewById<Chip>(R.id.young_version_chip)
        val redVersionChip = root.findViewById<Chip>(R.id.red_version_chip)
        val yellowVersionChip = root.findViewById<Chip>(R.id.yellow_version_chip)
        val blueVersionChip = root.findViewById<Chip>(R.id.blue_version_chip)
        val cardTextView = root.findViewById<TextView>(R.id.card_textview)
        val leftArrowImageView = root.findViewById<ImageView>(R.id.left_arrow)
        val rightArrowImageView = root.findViewById<ImageView>(R.id.right_arrow)
        val printingsRulingsChipGroup = root.findViewById<ChipGroup>(R.id.printings_rulings_chip_group)
        val rulingsLayout = root.findViewById<ConstraintLayout>(R.id.rulings_layout)
        val legalityRecyclerView = root.findViewById<RecyclerView>(R.id.legality_recyclerview)
        val rulingRecyclerView = root.findViewById<RecyclerView>(R.id.ruling_recyclerview)
        val printingsLayout = root.findViewById<ConstraintLayout>(R.id.printings_layout)
        val printingsRecyclerView = root.findViewById<RecyclerView>(R.id.printings_recyclerview)

        singleCardViewModel.cardPrinting.observe(viewLifecycleOwner, { it ->
            val version = it.version

            requireActivity().setActionBar(toolbar)
            requireActivity().actionBar?.title =
                getString(R.string.title_single_card, singleCardViewModel.cardPrinting.value?.name)

            GlideApp.with(requireContext())
                .asBitmap()
                .load(
                    Firebase.storage.reference
                        .child("card_images/" + singleCardViewModel.cardPrinting.value?.id + ".png")
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CropVerticalCardArt())
                .placeholder(R.drawable.horizontal_placeholder)
                .fallback(R.drawable.horizontal_placeholder)
                .into(cardImage)

            if (it.baseCard.pitch.isNotEmpty()) {
                pitchImageView.visibility = View.VISIBLE
                if (it.baseCard.pitch.size > version) {
                    it.getPitchSafe().let { pitch ->
                        when (pitch) {
                            0 -> pitchImageView.setImageResource(R.drawable.pitch_0)
                            1 -> pitchImageView.setImageResource(R.drawable.pitch_1)
                            2 -> pitchImageView.setImageResource(R.drawable.pitch_2)
                            3 -> pitchImageView.setImageResource(R.drawable.pitch_3)
                            else -> {
                                pitchImageView.setImageResource(R.drawable.pitch_0)
                            }
                        }
                    }
                } else {
                    pitchImageView.setImageResource(R.drawable.pitch_0)
                }
            } else {
                pitchImageView.visibility = View.GONE
            }

            if (it.getCostSafe() >= 0) {
                costTextView.visibility = View.VISIBLE
                costImageView.visibility = View.VISIBLE
                costTextView.text = it.baseCard.cost
            } else {
                costTextView.visibility = View.GONE
                costImageView.visibility = View.GONE
            }

            if (it.getIntellectSafe() > 0) {
                intelligenceTextView.visibility = View.VISIBLE
                intelligenceImageView.visibility = View.VISIBLE
                intelligenceTextView.text = it.baseCard.intellect
            } else {
                intelligenceTextView.visibility = View.GONE
                intelligenceImageView.visibility = View.GONE
            }
            if (it.getHealthSafe() > 0) {
                healthTextView.visibility = View.VISIBLE
                healthImageView.visibility = View.VISIBLE
                healthTextView.text = it.baseCard.health.toString()
            } else {
                healthTextView.visibility = View.GONE
                healthImageView.visibility = View.GONE
            }

            if (it.baseCard.power.isNotEmpty() && it.baseCard.power.size > version) {
                powerTextView.visibility = View.VISIBLE
                powerImageView.visibility = View.VISIBLE
                powerTextView.text = it.baseCard.power[version].toString()
            } else {
                powerTextView.visibility = View.GONE
                powerImageView.visibility = View.GONE
            }
            if (it.baseCard.defense.isNotEmpty() && it.baseCard.defense.size > version) {
                defenseTextView.visibility = View.VISIBLE
                defenseImageView.visibility = View.VISIBLE
                defenseTextView.text = it.baseCard.defense[version].toString()
            } else {
                defenseTextView.visibility = View.GONE
                defenseImageView.visibility = View.GONE
            }

            var cardText = it.baseCard.text

            if (cardText.isNotEmpty()) {
                if (it.baseCard.variablePower.isNotEmpty() && it.baseCard.variablePower.size > version)
                    cardText = cardText.replace("VARIABLE_POWER", it.baseCard.variablePower[version].toString())
                if (it.baseCard.variableDefense.isNotEmpty() && it.baseCard.variableDefense.size > version)
                    cardText = cardText.replace("VARIABLE_DEFENSE", it.baseCard.variableDefense[version].toString())
                if (it.baseCard.variableHealth.isNotEmpty() && it.baseCard.variableHealth.size > version)
                    cardText = cardText.replace("VARIABLE_HEALTH", it.baseCard.variableHealth[version].toString())
                if (it.baseCard.variableValue.isNotEmpty() && it.baseCard.variableValue.size > version)
                    cardText = cardText.replace("VARIABLE_VALUE", it.baseCard.variableValue[version].toString())
                if (it.baseCard.name.isNotEmpty())
                    cardText = cardText.replace("CARD_NAME", it.baseCard.name)

                val textViewHeight = cardTextView.lineHeight

                cardTextView.text = insertIconsIntoCardText(cardText, textViewHeight)
            }
                                                                      
            cardTypeTextView.text = it.baseCard.getFullTypeAsString()
            rarityTextView.text = it.rarity.toString()

            if (it.baseCard.pitch.isNotEmpty()) {
                versionsChipGroup.visibility = View.VISIBLE
                heroVersionsChipGroup.visibility = View.GONE
                for ((index, chip) in versionsChipGroup.children.withIndex()) {
                    if (containsVersion(singleCardViewModel.selectedVersions.value!!, index + 1))
                        chip.visibility = View.VISIBLE
                    else
                        chip.visibility = View.GONE
                }

                versionsChipGroup.setOnCheckedChangeListener(null)
                when (it.getPitchSafe()) {
                    1 -> redVersionChip.isChecked = true
                    2 -> yellowVersionChip.isChecked = true
                    3 -> blueVersionChip.isChecked = true
                }
                versionsChipGroup.setOnCheckedChangeListener(versionOnCheckedChangedListener)
            } else {
                versionsChipGroup.visibility = View.GONE
                heroVersionsChipGroup.visibility = View.GONE
            }
            legalityRecyclerView.suppressLayout(false)
            rulingRecyclerView.suppressLayout(false)
            printingsRecyclerView.suppressLayout(false)
            legalityRecyclerView.adapter?.notifyDataSetChanged()
            rulingRecyclerView.adapter?.notifyDataSetChanged()
            printingsRecyclerView.adapter?.notifyDataSetChanged()
            legalityRecyclerView.suppressLayout(true)
            rulingRecyclerView.suppressLayout(true)
            printingsRecyclerView.suppressLayout(true)

            if (singleCardViewModel.currentPosition.value ?: 0 < searchCardResultViewModel.cardPrintingList.value?.size?.minus(
                    1
                ) ?: 0
            )
                rightArrowImageView.visibility = View.VISIBLE
            else
                rightArrowImageView.visibility = View.GONE

            if (singleCardViewModel.currentPosition.value ?: 0 > 0)
                leftArrowImageView.visibility = View.VISIBLE
            else
                leftArrowImageView.visibility = View.GONE
        })

        printingsRulingsChipGroup.setOnCheckedChangeListener { chipGroup: ChipGroup, checkedId: Int ->

            when (chipGroup.checkedChipId) {
                R.id.printings_chip -> {
                    printingsLayout.visibility = View.VISIBLE
                    rulingsLayout.visibility = View.GONE
                }
                R.id.rulings_chip -> {
                    printingsLayout.visibility = View.GONE
                    rulingsLayout.visibility = View.VISIBLE
                }
            }
        }

        val formatList = deckListViewModel.formatList.value?.map { it.name }?.toMutableList() ?: mutableListOf()
        formatList.removeIf { it == "None" }

        val legalityRecyclerLayoutManager = GridLayoutManager(requireContext(), 2)
        val legalityRecyclerAdapter = LegalityRecyclerAdapter(
            formatList,
            singleCardViewModel.cardPrinting.value!!.baseCard.legalFormats,
            requireContext()
        )
        if (singleCardViewModel.cardPrinting.value!!.baseCard.legalFormats.isNotEmpty()) {
            legalityRecyclerView.adapter = legalityRecyclerAdapter
            legalityRecyclerView.layoutManager = legalityRecyclerLayoutManager
            legalityRecyclerView.suppressLayout(true)
        }

        val rulingRecyclerLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val rulingRecyclerAdapter =
            RulingRecyclerAdapter(
                singleCardViewModel.selectedRuling.value!!,
                ::insertIconsIntoCardText,
                requireContext()
            )

        if (singleCardViewModel.selectedRuling.value != null) {
            rulingRecyclerView.adapter = rulingRecyclerAdapter
            rulingRecyclerView.layoutManager = rulingRecyclerLayoutManager
            rulingRecyclerView.suppressLayout(true)
        }

        val printingsRecyclerLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val printingsRecyclerAdapter =
            PrintingsRecyclerAdapter(singleCardViewModel.sectionedPrintings.value!!, requireContext())

        if (singleCardViewModel.sectionedPrintings.value!!.isNotEmpty()) {
            printingsRecyclerView.adapter = printingsRecyclerAdapter
            printingsRecyclerView.layoutManager = printingsRecyclerLayoutManager
            printingsRecyclerView.suppressLayout(true)
        }

        rightArrowImageView.setOnClickListener {
            singleCardViewModel.currentPosition.value?.plus(1)
                ?.let { newPosition ->
                    singleCardViewModel.setCurrentPosition(newPosition)
                    searchCardResultViewModel.cardPrintingList.value?.get(newPosition)?.let { newCard ->
                        singleCardViewModel.setCardPrinting(
                            newCard,
                            searchCardResultViewModel.printingMap.value!!,
                            searchCardResultViewModel.cardMap.value!!,
                            searchCardResultViewModel.setMap.value!!,
                            false
                        )
                    }
                }
        }

        leftArrowImageView.setOnClickListener {
            singleCardViewModel.currentPosition.value?.minus(1)
                ?.let { newPosition ->
                    singleCardViewModel.setCurrentPosition(newPosition)
                    searchCardResultViewModel.cardPrintingList.value?.get(newPosition)?.let { newCard ->
                        singleCardViewModel.setCardPrinting(
                            newCard,
                            searchCardResultViewModel.printingMap.value!!,
                            searchCardResultViewModel.cardMap.value!!,
                            searchCardResultViewModel.setMap.value!!,
                            false
                        )
                    }
                }
        }

        return root
    }

    private val versionOnCheckedChangedListener =
        ChipGroup.OnCheckedChangeListener { chipGroup: ChipGroup, checkedId: Int ->
            when (checkedId) {
                R.id.red_version_chip -> {
                    singleCardViewModel.setCardPrinting(
                        singleCardViewModel.selectedVersions.value?.get(0)!!,
                        searchCardResultViewModel.printingMap.value!!,
                        searchCardResultViewModel.cardMap.value!!,
                        searchCardResultViewModel.setMap.value!!,
                        true
                    )
                }
                R.id.yellow_version_chip -> {
                    singleCardViewModel.setCardPrinting(
                        singleCardViewModel.selectedVersions.value?.get(1)!!,
                        searchCardResultViewModel.printingMap.value!!,
                        searchCardResultViewModel.cardMap.value!!,
                        searchCardResultViewModel.setMap.value!!,
                        true
                    )
                }
                R.id.blue_version_chip -> {
                    singleCardViewModel.setCardPrinting(
                        singleCardViewModel.selectedVersions.value?.get(2)!!,
                        searchCardResultViewModel.printingMap.value!!,
                        searchCardResultViewModel.cardMap.value!!,
                        searchCardResultViewModel.setMap.value!!,
                        true
                    )
                }
            }
        }
    private fun containsOnlyYoungHero(selectedVersions: MutableMap<Int, Printing>): Boolean {
        for ((index, cardPrinting) in selectedVersions) {
            if (cardPrinting.baseCard.subTypes.isEmpty()) {
                return false
            }
        }
        return true
    }

    private fun containsVersion(selectedVersions: MutableMap<Int, Printing>, versionToMatch: Int): Boolean {
        for ((index, cardPrinting) in selectedVersions) {
            if (cardPrinting.version < cardPrinting.baseCard.pitch.size) {
                if (cardPrinting.getPitchSafe() == versionToMatch)
                    return true
            }
        }
        return false
    }

    private fun insertIconsIntoCardText(cardText: String, textViewHeight: Int): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()

        powerDrawable.setBounds(0, 0, textViewHeight, textViewHeight)
        defenseDrawable.setBounds(0, 0, textViewHeight, textViewHeight)
        healthDrawable.setBounds(0, 0, textViewHeight, textViewHeight)
        intelligenceDrawable.setBounds(0, 0, textViewHeight, textViewHeight)
        resourceDrawable.setBounds(0, 0, textViewHeight, textViewHeight)

        val bracketRegex = Regex("(\\{[pdhir]\\})")
        var matchResult = bracketRegex.findAll(cardText)

        var endIndex = 0
        matchResult.forEach { match ->
            spannableStringBuilder.append(cardText.substring(endIndex, match.range.first))
            when {
                match.value.contains("p") -> spannableStringBuilder.append(" ", ImageSpan(powerDrawable, 0), 0)
                match.value.contains("d") -> spannableStringBuilder.append(" ", ImageSpan(defenseDrawable, 0), 0)
                match.value.contains("h") -> spannableStringBuilder.append(" ", ImageSpan(healthDrawable, 0), 0)
                match.value.contains("i") -> spannableStringBuilder.append(" ", ImageSpan(intelligenceDrawable, 0), 0)
                match.value.contains("r") -> spannableStringBuilder.append(" ", ImageSpan(resourceDrawable, 0), 0)
            }
            endIndex = match.range.last + 1
        }

        if (endIndex < cardText.length) {
            spannableStringBuilder.append(cardText.substring(endIndex))
        }

        return spannableStringBuilder
    }

}
