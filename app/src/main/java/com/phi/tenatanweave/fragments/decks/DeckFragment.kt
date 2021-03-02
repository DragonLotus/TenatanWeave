package com.phi.tenatanweave.fragments.decks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel
import com.phi.tenatanweave.fragments.dialogfragments.NewDeckDialogFragment
import com.phi.tenatanweave.recyclerviews.deckdisplayrecycler.DeckDisplayRecyclerAdapter

class DeckFragment : Fragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()
    private val deckListViewModel: DeckListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_deck, container, false)
        val deckDisplayRecyclerView: RecyclerView = root.findViewById(R.id.deck_recycler_view)
        val floatingActionButton = root.findViewById<FloatingActionButton>(R.id.deck_floating_action_button)

        val navController = findNavController()

        val textView: TextView = root.findViewById(R.id.not_logged_in_text)

        Firebase.auth.currentUser?.let {
            deckViewModel.setLoggedInUser(it)
            textView.visibility = View.GONE
            deckViewModel.setDatabaseDirectory(
                Firebase.database.reference
                    .child(resources.getString(R.string.db_collection_users))
                    .child(it.uid)
            )
            floatingActionButton.setOnClickListener{
                NewDeckDialogFragment().show(childFragmentManager, NewDeckDialogFragment.TAG)
            }
        }

        val deckDisplayLayoutManager = GridLayoutManager(requireContext(), 2)
        val deckDisplayRecyclerAdapter = DeckDisplayRecyclerAdapter(requireContext()) {
            val position = deckDisplayRecyclerView.getChildLayoutPosition(it as View)
            val deck = (deckDisplayRecyclerView.adapter as DeckDisplayRecyclerAdapter).getList()[position]

            deckListViewModel.setDeck(deck)

            navController.navigate(DeckFragmentDirections.actionNavigationDeckToDeckListFragment(position))
        }
        deckDisplayRecyclerView.layoutManager = deckDisplayLayoutManager
        deckDisplayRecyclerView.adapter = deckDisplayRecyclerAdapter
        deckViewModel.userDeckList.observe(viewLifecycleOwner, Observer {
            deckDisplayRecyclerAdapter.setList(it)
        })
        return root
    }
}
