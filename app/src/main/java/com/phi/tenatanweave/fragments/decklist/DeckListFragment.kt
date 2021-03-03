package com.phi.tenatanweave.fragments.decklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.decks.DeckViewModel
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.decklistrecycler.DeckListRecyclerAdapter

class DeckListFragment : Fragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()
    private val deckListViewModel: DeckListViewModel by activityViewModels()
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_deck_list, container, false)
        val deckListRecyclerView: RecyclerView = root.findViewById(R.id.deck_list_recycler_view)

        val deckListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val deckListRecyclerAdapter = DeckListRecyclerAdapter(deckListViewModel, requireContext())

        deckListRecyclerView.layoutManager = deckListLayoutManager
        deckListRecyclerView.adapter = deckListRecyclerAdapter
        deckListViewModel.deck.observe(viewLifecycleOwner, Observer {
            deckListViewModel.processDeck(searchCardResultViewModel.printingMap.value!!, searchCardResultViewModel.cardMap.value!!, requireContext())
        })
        deckListViewModel.sectionedDeckList.observe(viewLifecycleOwner,{
            deckListRecyclerAdapter.setList(it)
        })
        return root
    }
}
