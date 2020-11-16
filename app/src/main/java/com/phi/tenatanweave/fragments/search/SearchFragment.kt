package com.phi.tenatanweave.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.phi.tenatanweave.R
import com.phi.tenatanweave.customviews.EmptySearchView
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.viewpagers.searchviewpager.SearchSetViewPagerViewModel
import com.phi.tenatanweave.viewpagers.searchviewpager.SearchViewPagerAdapter

class SearchFragment : Fragment() {

    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()
    private val searchSetViewPagerViewModel: SearchSetViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        val navController = findNavController()

        val viewPager: ViewPager2 = root.findViewById(R.id.search_view_pager)
        viewPager.adapter = SearchViewPagerAdapter(activity!!)

        val tabLayout: TabLayout = root.findViewById(R.id.search_view_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Cards"
                1 -> tab.text = "Sets"
            }
        }.attach()

        val searchView = root.findViewById<EmptySearchView>(R.id.menu_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    when (viewPager.currentItem) {
                        0 -> {
                            searchCardResultViewModel.filterCards("")
                            val action =
                                SearchFragmentDirections.actionNavigationSearchToNavigationSearchCardResult(requireContext().getString(R.string.title_search_card_query_empty))
                            navController.navigate(action)
                        }
                        1 -> {
                            searchSetViewPagerViewModel.reset()
                        }
                    }
                } else if (query.isNotEmpty()) {
                    when (viewPager.currentItem) {
                        0 -> {
                            searchCardResultViewModel.filterCards(query)
//                            searchCardResultViewModel.filterCardsByName(query)
                            val action =
                                SearchFragmentDirections.actionNavigationSearchToNavigationSearchCardResult(query)
                            navController.navigate(action)
                        }
                        1 -> {
                            searchSetViewPagerViewModel.filterSetByName(query)
                        }
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    when (viewPager.currentItem) {
                        0 -> {
                        }
                        1 -> {
                            searchSetViewPagerViewModel.reset()
                        }
                    }
                } else if (newText.isNotEmpty()) {
                    when (viewPager.currentItem) {
                        1 -> {
                            searchSetViewPagerViewModel.filterSetByName(newText)
                        }
                    }
                }
                return true
            }

        })

//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
}
