package com.phi.tenatanweave.viewpagers.decklistviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.phi.tenatanweave.R

class DeckListStatsViewPagerFragment : Fragment() {
    private val deckListViewModel: DeckListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deck_list_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

    }

    companion object {
        private val ARG_CAUGHT = "myFragment_caught"

        fun getInstance(position: Int): DeckListStatsViewPagerFragment {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, position)
            val fragment = DeckListStatsViewPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}