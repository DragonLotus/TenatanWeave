package com.phi.tenatanweave.viewpagers.searchviewpager

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.phi.tenatanweave.R
import com.phi.tenatanweave.customviews.DisabledScrollView
import com.phi.tenatanweave.data.enums.CompareEnum
import com.phi.tenatanweave.data.enums.CompareTypeEnum
import com.phi.tenatanweave.data.enums.FilterStateEnum
import com.phi.tenatanweave.databinding.FragmentSearchCardOptionsBinding
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.cardtextrecycler.CardTextRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.comparecostrecycler.CostRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.comparedefenserecycler.CompareDefenseRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.comparepowerrecycler.ComparePowerRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.pitchrecycler.PitchRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.rarityrecycler.RarityRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.subtyperecycler.SubTypeRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.talentrecycler.TalentRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.typerecycler.TypeRecyclerAdapter

class SearchCardOptionsViewPagerFragment : Fragment() {

    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()
    lateinit var compareValueEditText: EditText
    lateinit var cardTextEditText: TextInputEditText
    lateinit var artistEditText: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = DataBindingUtil.inflate<FragmentSearchCardOptionsBinding>(
            inflater,
            R.layout.fragment_search_card_options,
            container,
            false
        ).apply {
            this.lifecycleOwner = lifecycleOwner
            this.viewmodel = searchCardResultViewModel
        }.root

        val constraintLayout = root.findViewById<ConstraintLayout>(R.id.search_options_constraint_layout)
        val scrollView = root.findViewById<DisabledScrollView>(R.id.search_scrollview)
//        scrollView.isNestedScrollingEnabled = false
        val typeRecyclerView = root.findViewById<RecyclerView>(R.id.type_recyclerview)
        val subTypeRecyclerView = root.findViewById<RecyclerView>(R.id.subtype_recyclerview)
        val talentRecyclerView = root.findViewById<RecyclerView>(R.id.talent_recyclerview)
        val pitchRecyclerView = root.findViewById<RecyclerView>(R.id.pitch_recyclerview)
        val costRecyclerView = root.findViewById<RecyclerView>(R.id.cost_recyclerview)
        val powerRecyclerView = root.findViewById<RecyclerView>(R.id.power_recyclerview)
        val defenseRecyclerView = root.findViewById<RecyclerView>(R.id.defense_recyclerview)
        val rarityRecyclerView = root.findViewById<RecyclerView>(R.id.rarity_recyclerview)
//        typeRecyclerView.setHasFixedSize(true)
//        subTypeRecyclerView.setHasFixedSize(true)
//        pitchRecyclerView.setHasFixedSize(true)
        costRecyclerView.setHasFixedSize(true)
        costRecyclerView.canScrollHorizontally(0)
        powerRecyclerView.setHasFixedSize(true)
        powerRecyclerView.canScrollHorizontally(0)
        defenseRecyclerView.setHasFixedSize(true)
        defenseRecyclerView.canScrollHorizontally(0)
        rarityRecyclerView.setHasFixedSize(true)
        val compareTypeChip = root.findViewById<Button>(R.id.compare_type_chip)
        val compareChip = root.findViewById<Button>(R.id.compare_chip)
        compareValueEditText = root.findViewById<EditText>(R.id.compare_edit_text)
        val addCompareButton = root.findViewById<Button>(R.id.add_compare_button)
        val cardTextInputLayout = root.findViewById<TextInputLayout>(R.id.card_text_input_layout)
        cardTextEditText = root.findViewById<TextInputEditText>(R.id.card_text_edit_text)
        val cardTextRecyclerView = root.findViewById<RecyclerView>(R.id.card_text_recyclerview)
        val artistInputLayout = root.findViewById<TextInputLayout>(R.id.artist_input_layout)
        artistEditText = root.findViewById<TextInputEditText>(R.id.artist_edit_text)
        val artistRecyclerView = root.findViewById<RecyclerView>(R.id.artist_recyclerview)
        val typeLabelTextView = root.findViewById<TextView>(R.id.type_label)

//        searchCardResultViewModel.typeList.observe(viewLifecycleOwner, Observer {
//            typeRecycler.adapter?.notifyDataSetChanged()
//        })
//        searchCardResultViewModel.pitchList.observe(viewLifecycleOwner, Observer {
//            pitchRecycler.adapter?.notifyDataSetChanged()
//        })
//        searchCardResultViewModel.compareCostList.observe(viewLifecycleOwner, Observer {
//            costRecyclerView.adapter?.notifyDataSetChanged()
//        })

        val typeLayoutManager = GridLayoutManager(context, 3)
        val typeRecyclerAdapter =
            TypeRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.typeList.value!!
            ) {
                clearEditTextFocus()
                val position = typeRecyclerView.getChildLayoutPosition(it.parent as View)
                val selectedFilterState =
                    searchCardResultViewModel.typeList.value!![position]

                if (position == 0 && (searchCardResultViewModel.typeList.value!!.contains(FilterStateEnum.NOT)
                            || searchCardResultViewModel.typeList.value!!.contains(FilterStateEnum.IS))
                ) {
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.clearSelectedTypes(typeRecyclerView.adapter!!)
                    }
                } else if (position != 0) {
                    activity!!.runOnUiThread {
                        if (searchCardResultViewModel.typeList.value!![0] != FilterStateEnum.NONE)
                            searchCardResultViewModel.setTypeFilterStateAtIndex(
                                0,
                                FilterStateEnum.NONE,
                                typeRecyclerView.adapter!!
                            )
                        searchCardResultViewModel.setTypeFilterStateAtIndex(
                            position,
                            FilterStateEnum.values()[(selectedFilterState.ordinal + 1) % FilterStateEnum.values().size],
                            typeRecyclerView.adapter!!
                        )
                    }

                    if (!searchCardResultViewModel.typeList.value!!.contains(FilterStateEnum.IS)
                        && !searchCardResultViewModel.typeList.value!!.contains(FilterStateEnum.NOT)
                    )
                        activity!!.runOnUiThread {
                            searchCardResultViewModel.setTypeFilterStateAtIndex(
                                0, FilterStateEnum.IS,
                                typeRecyclerView.adapter!!
                            )
                        }
                }
                typeRecyclerView.suppressLayout(false)
                typeRecyclerView.suppressLayout(true)
                scrollView.disableScroll = true
            }

        typeRecyclerView.layoutManager = typeLayoutManager
        typeRecyclerView.adapter = typeRecyclerAdapter
        typeRecyclerView.suppressLayout(true)

        val subTypeLayoutManager = GridLayoutManager(context, 3)
        val subTypeRecyclerAdapter =
            SubTypeRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.subTypeList.value!!
            ) {
                clearEditTextFocus()
                subTypeRecyclerView.suppressLayout(false)
                val position = subTypeRecyclerView.getChildLayoutPosition(it.parent as View)
                val selectedFilterState =
                    searchCardResultViewModel.subTypeList.value!![position]

                if (position == 0 && (searchCardResultViewModel.subTypeList.value!!.contains(FilterStateEnum.NOT)
                            || searchCardResultViewModel.subTypeList.value!!.contains(FilterStateEnum.IS))
                ) {
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.clearSelectedSubTypes(subTypeRecyclerView.adapter!!)
                    }
                } else if (position != 0) {

                    activity!!.runOnUiThread {
                        if (searchCardResultViewModel.subTypeList.value!![0] != FilterStateEnum.NONE)
                            searchCardResultViewModel.setSubTypeFilterStateAtIndex(
                                0,
                                FilterStateEnum.NONE,
                                subTypeRecyclerView.adapter!!
                            )
                        searchCardResultViewModel.setSubTypeFilterStateAtIndex(
                            position,
                            FilterStateEnum.values()[(selectedFilterState.ordinal + 1) % FilterStateEnum.values().size],
                            subTypeRecyclerView.adapter!!
                        )
                    }

                    if (!searchCardResultViewModel.subTypeList.value!!.contains(FilterStateEnum.IS)
                        && !searchCardResultViewModel.subTypeList.value!!.contains(FilterStateEnum.NOT)
                    )
                        activity!!.runOnUiThread {
                            searchCardResultViewModel.setSubTypeFilterStateAtIndex(
                                0,
                                FilterStateEnum.IS,
                                subTypeRecyclerView.adapter!!
                            )
                        }
                }
                subTypeRecyclerView.suppressLayout(true)
                scrollView.disableScroll = true
            }

        subTypeRecyclerView.layoutManager = subTypeLayoutManager
        subTypeRecyclerView.adapter = subTypeRecyclerAdapter
        subTypeRecyclerView.suppressLayout(true)

        val talentLayoutManager = GridLayoutManager(context, 3)
        val talentRecyclerAdapter =
            TalentRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.talentList.value!!
            ) {
                clearEditTextFocus()
                talentRecyclerView.suppressLayout(false)
                val position = talentRecyclerView.getChildLayoutPosition(it.parent as View)
                val selectedFilterState =
                    searchCardResultViewModel.talentList.value!![position]

                if (position == 0 && (searchCardResultViewModel.talentList.value!!.contains(FilterStateEnum.NOT)
                            || searchCardResultViewModel.talentList.value!!.contains(FilterStateEnum.IS))
                ) {
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.clearSelectedTalents(talentRecyclerView.adapter!!)
                    }
                } else if (position != 0) {

                    activity!!.runOnUiThread {
                        if (searchCardResultViewModel.talentList.value!![0] != FilterStateEnum.NONE)
                            searchCardResultViewModel.setTalentFilterStateAtIndex(
                                0,
                                FilterStateEnum.NONE,
                                talentRecyclerView.adapter!!
                            )
                        searchCardResultViewModel.setTalentFilterStateAtIndex(
                            position,
                            FilterStateEnum.values()[(selectedFilterState.ordinal + 1) % FilterStateEnum.values().size],
                            talentRecyclerView.adapter!!
                        )
                    }

                    if (!searchCardResultViewModel.talentList.value!!.contains(FilterStateEnum.IS)
                        && !searchCardResultViewModel.talentList.value!!.contains(FilterStateEnum.NOT)
                    )
                        activity!!.runOnUiThread {
                            searchCardResultViewModel.setTalentFilterStateAtIndex(
                                0,
                                FilterStateEnum.IS,
                                talentRecyclerView.adapter!!
                            )
                        }
                }
                talentRecyclerView.suppressLayout(true)
                scrollView.disableScroll = true
            }

        talentRecyclerView.layoutManager = talentLayoutManager
        talentRecyclerView.adapter = talentRecyclerAdapter
        talentRecyclerView.suppressLayout(true)

        val pitchLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val pitchRecyclerAdapter =
            PitchRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.pitchList.value!!
            ) {
                clearEditTextFocus()
                pitchRecyclerView.suppressLayout(false)
                val position = pitchRecyclerView.getChildLayoutPosition(it)
                val pitchSelected =
                    searchCardResultViewModel.pitchList.value!![position]

                searchCardResultViewModel.pitchList.value!![position] =
                    !pitchSelected
                activity!!.runOnUiThread {
                    pitchRecyclerView.adapter?.notifyItemChanged(position)
                    pitchRecyclerView.suppressLayout(true)
                    scrollView.disableScroll = true
                }
            }

        pitchRecyclerView.layoutManager = pitchLayoutManager
        pitchRecyclerView.adapter = pitchRecyclerAdapter
        pitchRecyclerView.suppressLayout(true)

        val costLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val costRecyclerAdapter =
            CostRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.compareCostList.value!!,
                View.OnClickListener {
                    clearEditTextFocus()
                    val position = typeRecyclerView.getChildLayoutPosition(it.parent as View)
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.removeCost(position, costRecyclerView)
                    }
                    view?.requestFocus()
                })

        costRecyclerView.layoutManager = costLayoutManager
        costRecyclerView.adapter = costRecyclerAdapter

        val powerLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val powerRecyclerAdapter =
            ComparePowerRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.comparePowerList.value!!,
                View.OnClickListener {
                    clearEditTextFocus()
                    val position = typeRecyclerView.getChildLayoutPosition(it.parent as View)
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.removePower(position, powerRecyclerView)
                    }
                    view?.requestFocus()
                })

        powerRecyclerView.layoutManager = powerLayoutManager
        powerRecyclerView.adapter = powerRecyclerAdapter

        val defenseLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val defenseRecyclerAdapter =
            CompareDefenseRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.compareDefenseList.value!!,
                View.OnClickListener {
                    clearEditTextFocus()
                    val position = typeRecyclerView.getChildLayoutPosition(it.parent as View)
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.removeDefense(position, defenseRecyclerView)
                    }
                    view?.requestFocus()
                })

        defenseRecyclerView.layoutManager = defenseLayoutManager
        defenseRecyclerView.adapter = defenseRecyclerAdapter

        compareTypeChip.setOnClickListener {
            clearEditTextFocus()
            searchCardResultViewModel.compareType.set(
                CompareTypeEnum.values()[searchCardResultViewModel.compareType.get()?.ordinal?.plus(
                    1
                )!! % CompareTypeEnum.values().size]
            )
            it.requestFocus()
            it.requestFocusFromTouch()
            it.clearFocus()
        }

        compareChip.setOnClickListener {
            clearEditTextFocus()
            searchCardResultViewModel.compare.set(
                CompareEnum.values()[searchCardResultViewModel.compare.get()?.ordinal?.plus(
                    1
                )!! % CompareEnum.values().size]
            )
//            it.requestFocus()
            it.requestFocusFromTouch()
            it.clearFocus()
        }

//        compareValueEditText.setOnEditorActionListener{ textView: TextView, i: Int, keyEvent: KeyEvent ->
//            if(i == EditorInfo.IME_ACTION_GO){
//            }
//            false
//        }

        compareValueEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                hideKeyboard()
        }

        cardTextEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                hideKeyboard()
        }

        artistEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                hideKeyboard()
        }

        cardTextEditText.setOnEditorActionListener { textView: TextView, i: Int, keyEvent: KeyEvent? ->
            if (i == EditorInfo.IME_ACTION_GO) {
                val text = cardTextEditText.text.toString().replace(";", "")
                if(text.isNotEmpty()){
                    searchCardResultViewModel.addCardText(text, cardTextRecyclerView)
                    cardTextRecyclerView.visibility = View.VISIBLE
                    realignViews(constraintLayout, cardTextInputLayout, cardTextRecyclerView, artistInputLayout)
                }
                cardTextEditText.setText("")
            }

            false
        }

        val cardTextLayoutManager = GridLayoutManager(context, 3)
        val cardTextRecyclerAdapter =
            CardTextRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.cardTextList.value!!
            ) {
                clearEditTextFocus()
                cardTextRecyclerView.suppressLayout(false)
                val position = cardTextRecyclerView.getChildLayoutPosition(it.parent as View)
                searchCardResultViewModel.removeCardText(position, cardTextRecyclerView)
                if (searchCardResultViewModel.cardTextList.value!!.size == 0) {
                    cardTextRecyclerView.visibility = View.GONE
                    realignViews(constraintLayout, cardTextInputLayout, cardTextRecyclerView, artistInputLayout)
                }

                cardTextRecyclerView.suppressLayout(true)
                scrollView.disableScroll = true
            }

        cardTextRecyclerView.layoutManager = cardTextLayoutManager
        cardTextRecyclerView.adapter = cardTextRecyclerAdapter
        cardTextRecyclerView.suppressLayout(true)
        if (searchCardResultViewModel.cardTextList.value!!.size > 0) {
            cardTextRecyclerView.visibility = View.VISIBLE
            realignViews(constraintLayout, cardTextInputLayout, cardTextRecyclerView, artistInputLayout)
        }

        artistEditText.setOnEditorActionListener { textView: TextView, i: Int, keyEvent: KeyEvent? ->
            if (i == EditorInfo.IME_ACTION_GO) {
                val text = artistEditText.text.toString().replace(";", "")
                if(text.isNotEmpty()){
                    searchCardResultViewModel.addArtist(text, artistRecyclerView)
                    artistRecyclerView.visibility = View.VISIBLE
                    realignViews(constraintLayout, artistInputLayout, artistRecyclerView, typeLabelTextView)
                }
                artistEditText.setText("")
            }

            false
        }

        val artistLayoutManager = GridLayoutManager(context, 3)
        val artistRecyclerAdapter =
            CardTextRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.artistList.value!!
            ) {
                clearEditTextFocus()
                artistRecyclerView.suppressLayout(false)
                val position = artistRecyclerView.getChildLayoutPosition(it.parent as View)
                searchCardResultViewModel.removeArtist(position, artistRecyclerView)
                if (searchCardResultViewModel.artistList.value!!.size == 0) {
                    artistRecyclerView.visibility = View.GONE
                    realignViews(constraintLayout, artistInputLayout, artistRecyclerView, typeLabelTextView)
                }

                artistRecyclerView.suppressLayout(true)
                scrollView.disableScroll = true
            }

        artistRecyclerView.layoutManager = artistLayoutManager
        artistRecyclerView.adapter = artistRecyclerAdapter
        artistRecyclerView.suppressLayout(true)
        if (searchCardResultViewModel.artistList.value!!.size > 0) {
            artistRecyclerView.visibility = View.VISIBLE
            realignViews(constraintLayout, artistInputLayout, artistRecyclerView, typeLabelTextView)
        }

        addCompareButton.setOnClickListener {
            activity?.runOnUiThread {
                clearEditTextFocus()
                when (searchCardResultViewModel.compareType.get()) {
                    CompareTypeEnum.COST -> {
                        searchCardResultViewModel.addCost(costRecyclerView)
                    }
                    CompareTypeEnum.POWER -> {
                        searchCardResultViewModel.addPower(powerRecyclerView)
                    }
                    CompareTypeEnum.DEFENSE -> {
                        searchCardResultViewModel.addDefense(defenseRecyclerView)
                    }
                }
            }
            it.requestFocus()
            it.requestFocusFromTouch()
            it.clearFocus()
        }

        val rarityLayoutManager = GridLayoutManager(context, 3)
        val rarityRecyclerAdapter =
            RarityRecyclerAdapter(
                requireContext(),
                searchCardResultViewModel.rarityList.value!!
            ) {
                clearEditTextFocus()
//                rarityRecyclerView.suppressLayout(false)
                val position = rarityRecyclerView.getChildLayoutPosition(it.parent as View)
                val selectedFilterState =
                    searchCardResultViewModel.rarityList.value!![position]

                if (position == 0 && (searchCardResultViewModel.rarityList.value!!.contains(FilterStateEnum.NOT)
                            || searchCardResultViewModel.rarityList.value!!.contains(FilterStateEnum.IS))
                ) {
                    activity!!.runOnUiThread {
                        searchCardResultViewModel.clearSelectedRarity(rarityRecyclerView.adapter!!)
                    }
                } else if (position != 0) {

                    activity!!.runOnUiThread {
                        if (searchCardResultViewModel.rarityList.value!![0] != FilterStateEnum.NONE)
                            searchCardResultViewModel.setRarityFilterStateAtIndex(
                                0,
                                FilterStateEnum.NONE,
                                rarityRecyclerView.adapter!!
                            )
                        searchCardResultViewModel.setRarityFilterStateAtIndex(
                            position,
                            FilterStateEnum.values()[(selectedFilterState.ordinal + 1) % FilterStateEnum.values().size],
                            rarityRecyclerView.adapter!!
                        )
                    }

                    if (!searchCardResultViewModel.rarityList.value!!.contains(FilterStateEnum.IS)
                        && !searchCardResultViewModel.rarityList.value!!.contains(FilterStateEnum.NOT)
                    )
                        activity!!.runOnUiThread {
                            searchCardResultViewModel.setRarityFilterStateAtIndex(
                                0,
                                FilterStateEnum.IS,
                                rarityRecyclerView.adapter!!
                            )
                        }
                }
//                rarityRecyclerView.suppressLayout(true)
                scrollView.disableScroll = true
            }

        rarityRecyclerView.layoutManager = rarityLayoutManager
        rarityRecyclerView.adapter = rarityRecyclerAdapter
//        rarityRecyclerView.suppressLayout(true)

        return root
    }

    private fun realignViews(constraintLayout: ConstraintLayout, viewAbove: View, viewMiddle: View, viewBelow: View) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (viewMiddle.visibility == View.VISIBLE) {
            constraintSet.connect(viewAbove.id, ConstraintSet.BOTTOM, viewMiddle.id, ConstraintSet.TOP, 0)
            constraintSet.connect(viewMiddle.id, ConstraintSet.TOP, viewAbove.id, ConstraintSet.BOTTOM, 0)
            constraintSet.connect(viewMiddle.id, ConstraintSet.BOTTOM, viewBelow.id, ConstraintSet.TOP, 0)
            constraintSet.connect(viewBelow.id, ConstraintSet.TOP, viewMiddle.id, ConstraintSet.BOTTOM, 0)
            constraintSet.applyTo(constraintLayout)
        } else if (viewMiddle.visibility == View.GONE) {
            constraintSet.connect(viewAbove.id, ConstraintSet.BOTTOM, viewBelow.id, ConstraintSet.TOP, 0)
            constraintSet.connect(viewBelow.id, ConstraintSet.TOP, viewAbove.id, ConstraintSet.BOTTOM, 0)
            constraintSet.applyTo(constraintLayout)
        }
    }

    private fun clearEditTextFocus() {
        compareValueEditText.clearFocus()
        cardTextEditText.clearFocus()
        artistEditText.clearFocus()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        arguments?.takeIf {
//            it.containsKey("object")
//        }?.apply {
//            val textView: TextView = view.findViewById(R.id.search_card_options_text)
//            textView.text = getInt("object").toString()
//        }
    }

    //    companion object {
//        private val ARG_CAUGHT = "myFragment_caught"
//
//        fun getInstance(position: Int): SearchCardOptionsViewPagerFragment {
//            val args: Bundle = Bundle()
//            args.putSerializable(ARG_CAUGHT, position)
//            val fragment = SearchCardOptionsViewPagerFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }

    companion object BinderAdapter {


        @BindingAdapter("android:context", "android:backgroundColor")
        @JvmStatic
        fun setBackgroundColor(view: View, context: Context, backgroundColor: Int) {
            view.setBackgroundColor(context.resources.getColor(backgroundColor))
        }

        @BindingAdapter("android:selectedItemPosition")
        @JvmStatic
        fun setSelectedItemPosition(view: AdapterView<*>, position: Int) {
            if (view.selectedItemPosition != position) {
                view.setSelection(position)
            }
        }

        @BindingAdapter(
            value = ["android:onItemSelected", "android:onNothingSelected", "android:selectedItemPositionAttrChanged"],
            requireAll = false
        )
        @JvmStatic
        fun setOnItemSelectedListener(
            view: Spinner, selected: AdapterViewBindingAdapter.OnItemSelected?,
            nothingSelected: AdapterViewBindingAdapter.OnNothingSelected?, attrChanged: InverseBindingListener?
        ) {
            if (selected == null && nothingSelected == null && attrChanged == null) {
                view.onItemSelectedListener = null
            } else {
                view.onItemSelectedListener =
                    AdapterViewBindingAdapter.OnItemSelectedComponentListener(selected, nothingSelected, attrChanged)
            }
        }

        @InverseBindingAdapter(
            attribute = "android:selectedItemPosition",
            event = "android:selectedItemPositionAttrChanged"
        )
        @JvmStatic
        fun getSelectedItemPosition(view: Spinner): Int {
            return view.selectedItemPosition
        }
    }

}