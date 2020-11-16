package com.phi.tenatanweave.viewpagers.searchviewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SearchViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                val fragment = SearchCardOptionsViewPagerFragment()
                fragment.arguments = Bundle().apply {
                    putInt("object", position + 1)
                }
                return fragment
            }
            1 -> {
                val fragment = SearchSetViewPagerFragment()
                fragment.arguments = Bundle().apply {
                    putInt("object", position + 1)
                }
                return fragment
            }
            else -> {
                val fragment = SearchCardOptionsViewPagerFragment()
                fragment.arguments = Bundle().apply {
                    putInt("object", position + 1)
                }
                return fragment
            }
        }
    }
}