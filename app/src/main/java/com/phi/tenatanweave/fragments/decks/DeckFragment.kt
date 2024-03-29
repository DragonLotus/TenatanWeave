package com.phi.tenatanweave.fragments.decks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
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
import com.phi.tenatanweave.fragments.dialogfragments.DeckOptionsBottomSheetFragment
import com.phi.tenatanweave.viewpagers.decklistviewpager.DeckListViewModel
import com.phi.tenatanweave.fragments.dialogfragments.DeckDetailsDialogFragment
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

        val toolbar: Toolbar = root.findViewById(R.id.toolbar)
        requireActivity().setActionBar(toolbar)
        requireActivity().actionBar?.title = getString(R.string.title_decks)

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
                DeckDetailsDialogFragment.newInstance(Bundle()).show(requireActivity().supportFragmentManager, this.tag)
            }
        }

        val deckDisplayLayoutManager = GridLayoutManager(requireContext(), 2)
        val deckDisplayRecyclerAdapter = DeckDisplayRecyclerAdapter(requireContext(), {
            val position = deckDisplayRecyclerView.getChildLayoutPosition(it as View)
            val deck = (deckDisplayRecyclerView.adapter as DeckDisplayRecyclerAdapter).getList()[position]

            deckListViewModel.setDeck(deck)

            navController.navigate(DeckFragmentDirections.actionNavigationDeckToDeckListFragment(position))
        }, {
            val position = deckDisplayRecyclerView.getChildLayoutPosition(it as View)

            val bundle = Bundle()
            bundle.putInt(getString(R.string.deck_index), position)
            DeckOptionsBottomSheetFragment.newInstance(bundle).show(requireActivity().supportFragmentManager, this.tag)
            true
        })
        deckDisplayRecyclerView.layoutManager = deckDisplayLayoutManager
        deckDisplayRecyclerView.adapter = deckDisplayRecyclerAdapter
        deckViewModel.userDeckList.observe(viewLifecycleOwner, Observer {
            deckDisplayRecyclerAdapter.setList(it)
        })
        return root
    }
}
