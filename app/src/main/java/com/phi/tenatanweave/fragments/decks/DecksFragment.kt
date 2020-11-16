package com.phi.tenatanweave.fragments.decks

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel

class DecksFragment : Fragment() {

    private lateinit var decksViewModel: DecksViewModel
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        decksViewModel =
                ViewModelProviders.of(this).get(DecksViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_deck, container, false)

        val textView: TextView = root.findViewById(R.id.text_deck)
        decksViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
