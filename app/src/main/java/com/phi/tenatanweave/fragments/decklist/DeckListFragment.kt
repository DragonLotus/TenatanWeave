package com.phi.tenatanweave.fragments.decklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.phi.tenatanweave.R
import com.phi.tenatanweave.viewpagers.decklistviewpager.DeckListViewModel
import com.phi.tenatanweave.viewpagers.decklistviewpager.DeckListViewPagerAdapter

class DeckListFragment : Fragment() {

    private val deckListViewModel: DeckListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_deck_list_container, container, false)

        val navController = findNavController()

        val viewPager: ViewPager2 = root.findViewById(R.id.deck_list_view_pager)
        viewPager.adapter = DeckListViewPagerAdapter(activity!!)

        val tabLayout: TabLayout = root.findViewById(R.id.deck_list_view_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Deck"
                1 -> tab.text = "Stats"
            }
        }.attach()
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
}
