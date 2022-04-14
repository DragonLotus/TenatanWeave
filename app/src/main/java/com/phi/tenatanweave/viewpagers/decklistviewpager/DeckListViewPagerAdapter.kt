package com.phi.tenatanweave.viewpagers.decklistviewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phi.tenatanweave.fragments.decks.DeckFragment

class DeckListViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                val fragment = DeckListViewPagerFragment()
                fragment.arguments = Bundle(1).apply {
                    putInt("object", position + 1)
                }
                return fragment
            }
            1 -> {
                val fragment = DeckListStatsViewPagerFragment()
                fragment.arguments = Bundle(1).apply {
                    putInt("object", position + 1)
                }
                return fragment
            }
            else -> {
                val fragment = DeckListViewPagerFragment()
                fragment.arguments = Bundle(1).apply {
                    putInt("object", position + 1)
                }
                return fragment
            }
        }
    }
}