package com.phi.tenatanweave.fragments.decklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.customviews.EmptySearchView
import com.phi.tenatanweave.data.AdapterUpdate
import com.phi.tenatanweave.data.RecyclerItem
import com.phi.tenatanweave.fragments.decks.DeckViewModel
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.decklistcardsearchrecycler.DeckListCardSearchRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.decklistrecycler.DeckListRecyclerAdapter
import kotlinx.android.synthetic.main.deck_list_detail_linear_row.*

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
        val deckListCardSearchRecyclerView: RecyclerView = root.findViewById(R.id.deck_list_card_search_recycler_view)

        val deckListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val deckListCardSearchLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val deckListRecyclerAdapter = DeckListRecyclerAdapter(deckListViewModel, requireContext(), {
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position] as RecyclerItem.CardPrinting

            val indicesToUpdateList = deckListViewModel.increaseQuantity(position, item.cardPrinting, requireContext())
            for (index in indicesToUpdateList){
                when(index){
                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
                    is AdapterUpdate.Remove -> adapter.removeItem(index.index)
                }
            }
        }, {
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position] as RecyclerItem.CardPrinting

            val indicesToUpdateList = deckListViewModel.decreaseQuantity(position, item.cardPrinting, requireContext())
            for (index in indicesToUpdateList){
                when(index){
                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
                    is AdapterUpdate.Remove ->  adapter.removeItem(index.index)
                }
            }
        })
        val deckListCardSearchRecyclerAdapter = DeckListCardSearchRecyclerAdapter(deckListViewModel, requireContext(), {
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position] as RecyclerItem.CardPrinting

            val indicesToUpdateList = deckListViewModel.increaseQuantity(position, item.cardPrinting, requireContext())
//            for (index in indicesToUpdateList){
//                when(index){
//                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
//                    is AdapterUpdate.Remove -> adapter.removeItem(index.index)
//                }
//            }
        }, {
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position] as RecyclerItem.CardPrinting

            val indicesToUpdateList = deckListViewModel.decreaseQuantity(position, item.cardPrinting, requireContext())
//            for (index in indicesToUpdateList){
//                when(index){
//                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
//                    is AdapterUpdate.Remove ->  adapter.removeItem(index.index)
//                }
//            }
        })

        deckListRecyclerView.layoutManager = deckListLayoutManager
        deckListRecyclerView.adapter = deckListRecyclerAdapter
        deckListViewModel.deck.observe(viewLifecycleOwner, Observer {
            deckListViewModel.processDeck(
                searchCardResultViewModel.printingMap.value!!,
                searchCardResultViewModel.cardMap.value!!,
                searchCardResultViewModel.masterCardPrintingList,
                requireContext()
            )
        })
        deckListViewModel.sectionedDeckList.observe(viewLifecycleOwner, {
            deckListRecyclerAdapter.setList(it)
        })

        deckListCardSearchRecyclerView.layoutManager = deckListCardSearchLayoutManager
        deckListCardSearchRecyclerView.adapter = deckListCardSearchRecyclerAdapter
        deckListViewModel.deckListCardSearchList.observe(viewLifecycleOwner, {
            deckListCardSearchRecyclerAdapter.setList(it)
        })

        val deckListCardSearchView = root.findViewById<EmptySearchView>(R.id.deck_list_card_search)
        deckListCardSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                deckListCardSearchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    deckListRecyclerView.visibility = View.VISIBLE
                    deckListCardSearchRecyclerView.visibility = View.INVISIBLE
                } else if (newText.isNotEmpty()) {
                    deckListViewModel.filterCardsPrioritizingPrintings(
                        searchCardResultViewModel.masterCardPrintingList,
                        newText.toString()
                    )
                    deckListRecyclerView.visibility = View.INVISIBLE
                    deckListCardSearchRecyclerView.visibility = View.VISIBLE
                }
                return true
            }

        })
        return root
    }
}
