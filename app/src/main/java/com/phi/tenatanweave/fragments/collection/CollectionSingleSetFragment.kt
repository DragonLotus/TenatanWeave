package com.phi.tenatanweave.fragments.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.collectioncardlistrecycler.CollectionCardListRecyclerAdapter


class CollectionSingleSetFragment : Fragment() {

    private val collectionViewModel: CollectionViewModel by activityViewModels()
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()
    private val args: CollectionSingleSetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_collection_single_set, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        activity?.setActionBar(toolbar)
        activity?.actionBar?.title = args.query

        val collectionCardListRecyclerView: RecyclerView = view.findViewById(R.id.collection_card_list_recycler_view)
        val collectionCardListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val collectionCardListRecyclerAdapter = CollectionCardListRecyclerAdapter(requireContext(), collectionViewModel::updateOrAddCollectionEntry)

        collectionCardListRecyclerView.layoutManager = collectionCardListLayoutManager
        collectionCardListRecyclerView.adapter = collectionCardListRecyclerAdapter

        collectionViewModel.printingList.observe(viewLifecycleOwner, {
            collectionCardListRecyclerAdapter.setList(it)
        })
    }
}
