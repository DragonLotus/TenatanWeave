package com.phi.tenatanweave.fragments.dialogfragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phi.tenatanweave.R
import com.phi.tenatanweave.customviews.EmptySearchView
import com.phi.tenatanweave.fragments.decks.DeckViewModel

class CardPrintingsBottomSheetFragment : BottomSheetDialogFragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_card_printing, container, false)
        val cardPrintingRecyclerVew: RecyclerView = root.findViewById(R.id.card_printing_recycler_view)
        val cardPrintingSearchView = root.findViewById<EmptySearchView>(R.id.card_printing_search)

        val cardPrintingRecyclerViewLayoutManager = object: LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false){
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                if (lp != null) {
                    lp.height = height / 3
                }
                return super.checkLayoutParams(lp)
            }
        }
        cardPrintingRecyclerVew.layoutManager = cardPrintingRecyclerViewLayoutManager

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        if (bundle != null)
            setUpViews(bundle)
        else
            setUpViews(Bundle())
    }

    private fun setUpViews(bundle: Bundle) {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
    }

    private var mListener: CardPrintingItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CardPrintingItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface CardPrintingItemClickListener {
        fun onItemClick(item: String, bundle: Bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): CardPrintingsBottomSheetFragment {
            val fragment = CardPrintingsBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}