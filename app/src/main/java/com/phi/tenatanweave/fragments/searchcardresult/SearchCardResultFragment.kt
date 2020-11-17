package com.phi.tenatanweave.fragments.searchcardresult

import android.os.Bundle
import android.view.*
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CardPrinting
import com.phi.tenatanweave.recyclerviews.cardrecycler.CardRecyclerAdapter

class SearchCardResultFragment : Fragment() {

    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()
    private val args: SearchCardResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search_card_result, container, false)
        val cardResultRecyclerView: RecyclerView = root.findViewById(R.id.card_result_recycler_view)

        val toolbar : Toolbar = root.findViewById(R.id.toolbar)
        activity?.setActionBar(toolbar)
        activity?.actionBar?.title = getString(R.string.title_search_card_query, args.query)

        val navController = findNavController()

        searchCardResultViewModel.cardPrintingList.observe(viewLifecycleOwner, Observer<List<CardPrinting>> {
            cardResultRecyclerView.adapter?.notifyDataSetChanged()
        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)

        cardResultRecyclerView.layoutManager = gridLayoutManager
        cardResultRecyclerView.adapter = CardRecyclerAdapter(searchCardResultViewModel.cardPrintingList.value!!, requireContext(),true) {
            val position = cardResultRecyclerView.getChildLayoutPosition(it as View)
            val card = (cardResultRecyclerView.adapter as CardRecyclerAdapter).getList()[position]

            navController.navigate(SearchCardResultFragmentDirections.actionNavigationSearchCardResultToNavigationSingleCard(position))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchCardResultViewModel.cardNameQuery.clear()
        searchCardResultViewModel.setNameQuery.clear()
    }
}
