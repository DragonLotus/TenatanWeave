package com.phi.tenatanweave.viewpagers.searchviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.search.SearchFragmentDirections
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.setchildrecyclerview.SetChildRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.setrecycler.SetRecyclerAdapter
import kotlinx.android.synthetic.main.set_detail_row.view.*

class SearchSetViewPagerFragment : Fragment() {
    private val searchSetViewPagerViewModel: SearchSetViewPagerViewModel by activityViewModels()
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

//        arguments?.takeIf {
//            it.containsKey("object")
//        }?.apply {
//            val textView: TextView = view.findViewById(R.id.searchViewCardText)
//            textView.text = getInt("object").toString()
//        }
        val setRecyclerView: RecyclerView = view.findViewById(R.id.set_list_recycler_view)

        searchCardResultViewModel.expansionSetMap.observe(viewLifecycleOwner, {
            searchSetViewPagerViewModel.setExpansionSetMap(it)
        })

        searchSetViewPagerViewModel.expansionSetDisplayMap.observe(viewLifecycleOwner, {
            (setRecyclerView.adapter as SetRecyclerAdapter).notifyDataSetChangedAndUpdate()
        })

        val linearLayoutManager = LinearLayoutManager(context)
        setRecyclerView.adapter =
            SetRecyclerAdapter(requireContext(), searchSetViewPagerViewModel.expansionSetDisplayMap.value!!, {
                val expansionSet =
                    (setRecyclerView.adapter as SetRecyclerAdapter).getList()[setRecyclerView.getChildLayoutPosition(it)]
                searchCardResultViewModel.filterCardsBySet(expansionSet.setCode)

                val action =
                    SearchFragmentDirections.actionNavigationSearchToNavigationSearchCardResult(expansionSet.name)
                navController.navigate(action)
            }, {
                //This is so hacky. I can't believe this worked.
                val expansionSet =
                    ((setRecyclerView.findContainingViewHolder(it)!!.itemView.child_set_recycler_view).adapter as SetChildRecyclerAdapter).getList()[setRecyclerView.getChildLayoutPosition(it)]
                searchCardResultViewModel.filterCardsBySet(expansionSet.setCode)

                val action =
                    SearchFragmentDirections.actionNavigationSearchToNavigationSearchCardResult(expansionSet.name)
                navController.navigate(action)

            }, {
                val position = setRecyclerView.getChildLayoutPosition(it)
                (setRecyclerView.adapter as SetRecyclerAdapter).setExpandedPosition(position)
            })
        setRecyclerView.layoutManager = linearLayoutManager

    }

    companion object {
        private val ARG_CAUGHT = "myFragment_caught"

        fun getInstance(position: Int): SearchSetViewPagerFragment {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, position)
            val fragment = SearchSetViewPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchSetViewPagerViewModel.setNameQuery.clear()
    }

}