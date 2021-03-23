package com.phi.tenatanweave.fragments.dialogfragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.decks.DeckViewModel
import kotlinx.android.synthetic.main.bottom_sheet_card.*

class CardOptionsBottomSheetFragment : BottomSheetDialogFragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_card, container, false)
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
        printings_text_view.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(requireActivity().getString(R.string.card_options_printings), bundle)
        }
        details_text_view.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(requireActivity().getString(R.string.card_options_details), bundle)
        }
    }

    private var mListener: CardOptionsItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CardOptionsItemClickListener) {
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

    interface CardOptionsItemClickListener {
        fun onItemClick(item: String, bundle: Bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): CardOptionsBottomSheetFragment {
            val fragment = CardOptionsBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}