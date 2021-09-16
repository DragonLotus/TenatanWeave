package com.phi.tenatanweave.viewpagers.decklistviewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.phi.tenatanweave.R
import com.phi.tenatanweave.customviews.EmptySearchView
import com.phi.tenatanweave.data.AdapterUpdate
import com.phi.tenatanweave.data.RecyclerItem
import com.phi.tenatanweave.fragments.decks.DeckViewModel
import com.phi.tenatanweave.fragments.dialogfragments.CardOptionsBottomSheetFragment
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.decklistcardsearchrecycler.DeckListCardSearchRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.decklistrecycler.DeckListRecyclerAdapter

class DeckListViewPagerFragment : Fragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()
    private val deckListViewModel: DeckListViewModel by activityViewModels()
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_deck_list, container, false)
        val deckListConstraintLayout = root.findViewById<ConstraintLayout>(R.id.deck_list_constraint_layout)
        val deckListAppBar = root.findViewById<AppBarLayout>(R.id.app_bar)
        val deckListRecyclerView: RecyclerView = root.findViewById(R.id.deck_list_recycler_view)
        val deckListCardSearchRecyclerView: RecyclerView = root.findViewById(R.id.deck_list_card_search_recycler_view)

        val toolbar: Toolbar = root.findViewById(R.id.toolbar)
        requireActivity().setActionBar(toolbar)
        requireActivity().actionBar?.title = deckListViewModel.deck.value?.deckName
        val deckListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val deckListCardSearchLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val deckListRecyclerAdapter = DeckListRecyclerAdapter(deckListViewModel, requireContext(), {
            //Increase
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position] as RecyclerItem.Printing

            val indicesToUpdateList = deckListViewModel.increaseQuantity(position, item.printing, requireContext())
            for (index in indicesToUpdateList) {
                when (index) {
                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
                    is AdapterUpdate.Remove -> adapter.removeItem(index.index)
                }
            }
            deckListViewModel.deck.value?.let { deckToUpdate -> deckViewModel.updateOrAddDeck(deckToUpdate, resources) }
        }, {
            //Decrease
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position] as RecyclerItem.Printing

            val indicesToUpdateList = deckListViewModel.decreaseQuantity(position, item.printing, requireContext())
            for (index in indicesToUpdateList) {
                when (index) {
                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
                    is AdapterUpdate.Remove -> adapter.removeItem(index.index)
                }
            }
            deckListViewModel.deck.value?.let { deckToUpdate -> deckViewModel.updateOrAddDeck(deckToUpdate, resources) }
        }, {
            //Hero onClick
//            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
//            val position = deckListRecyclerView.getChildLayoutPosition(it as View)

            deckListViewModel.isHeroSearchMode = true
            deckListViewModel.setupHeroSearch(searchCardResultViewModel.masterCardPrintingList)
            deckListRecyclerView.visibility = View.INVISIBLE
            deckListCardSearchRecyclerView.visibility = View.VISIBLE
        }, {
            //Show Card Options onClick
            val adapter = deckListRecyclerView.adapter as DeckListRecyclerAdapter
            val position = deckListRecyclerView.getChildLayoutPosition(it as View)

            val bundle = Bundle()
            bundle.putInt(getString(R.string.card_index), position)
            CardOptionsBottomSheetFragment.newInstance(bundle).show(requireActivity().supportFragmentManager, this.tag)
        })
        val deckListCardSearchRecyclerAdapter = DeckListCardSearchRecyclerAdapter(deckListViewModel, {
            //Increase from search
            val adapter = deckListCardSearchRecyclerView.adapter as DeckListCardSearchRecyclerAdapter
            val position = deckListCardSearchRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position]

            val indicesToUpdateList = deckListViewModel.increaseQuantityFromSearch(position, item, requireContext())
            for (index in indicesToUpdateList) {
                when (index) {
                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
                    is AdapterUpdate.Remove -> adapter.removeItem(index.index)
                }
            }
            deckListViewModel.deck.value?.let { deckToUpdate -> deckViewModel.updateOrAddDeck(deckToUpdate, resources) }
        }, {
            //Decrease from search
            val adapter = deckListCardSearchRecyclerView.adapter as DeckListCardSearchRecyclerAdapter
            val position = deckListCardSearchRecyclerView.getChildLayoutPosition(it.parent as View)
            val item = adapter.getList()[position]

            val indicesToUpdateList = deckListViewModel.decreaseQuantityFromSearch(position, item, requireContext())
            for (index in indicesToUpdateList) {
                when (index) {
                    is AdapterUpdate.Changed -> adapter.notifyItemChanged(index.index)
                    is AdapterUpdate.Remove -> adapter.removeItem(index.index)
                }
            }
            deckListViewModel.deck.value?.let { deckToUpdate -> deckViewModel.updateOrAddDeck(deckToUpdate, resources) }
        }, {
            //Hero onClick from search
            val adapter = deckListCardSearchRecyclerView.adapter as DeckListCardSearchRecyclerAdapter
            val position = deckListCardSearchRecyclerView.getChildLayoutPosition(it as View)
            val item = adapter.getList()[position]

            deckListViewModel.setHero(item)
            deckListRecyclerView.visibility = View.VISIBLE
            deckListCardSearchRecyclerView.visibility = View.INVISIBLE
            deckListViewModel.isHeroSearchMode = false

            deckListViewModel.deck.value?.let { deckToUpdate -> deckViewModel.updateOrAddDeck(deckToUpdate, resources) }
        })

        deckListRecyclerView.layoutManager = deckListLayoutManager
        deckListRecyclerView.adapter = deckListRecyclerAdapter

        deckListViewModel.deck.observe(viewLifecycleOwner, Observer {
            deckListViewModel.processDeck(
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
                if (!deckListViewModel.isHeroSearchMode) {
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
                } else {
                    if (!newText.isNullOrEmpty())
                        deckListViewModel.filterHeroCards(newText)
                }

                return true
            }

        })

        val searchViewCloseButton: ImageView =
            deckListCardSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        searchViewCloseButton.setOnClickListener {
            Log.d("SearchViewOnClose", "SearchView OnClickListener")
            Log.d("SearchViewOnClose", "Hero mode is: ${deckListViewModel.isHeroSearchMode}")
            if (deckListViewModel.isHeroSearchMode)
                deckListViewModel.isHeroSearchMode = false
            deckListCardSearchView.clearFocus()
            deckListCardSearchView.isIconified = true
        }

        root?.post(Runnable {
            val deckListRecyclerViewHeight = deckListConstraintLayout.height - deckListAppBar.height - deckListCardSearchView.height
            deckListRecyclerView.layoutParams.height = deckListRecyclerViewHeight
            deckListCardSearchRecyclerView.layoutParams.height = deckListRecyclerViewHeight
        })

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
