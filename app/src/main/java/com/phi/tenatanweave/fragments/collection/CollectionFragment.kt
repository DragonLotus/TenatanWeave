package com.phi.tenatanweave.fragments.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.dialogfragments.DeckDetailsDialogFragment
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.recyclerviews.setchildrecyclerview.SetChildRecyclerAdapter
import com.phi.tenatanweave.recyclerviews.setrecycler.SetRecyclerAdapter
import kotlinx.android.synthetic.main.set_detail_row.view.*


class CollectionFragment : Fragment() {

    private val collectionViewModel: CollectionViewModel by activityViewModels()
    private val searchCardResultViewModel: SearchCardResultViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_collection, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        Firebase.auth.currentUser?.let {
            collectionViewModel.setDatabaseDirectory(
                Firebase.database.reference
                    .child(resources.getString(R.string.db_collection_users))
                    .child(it.uid)
            )
        }

        val setRecyclerView: RecyclerView = view.findViewById(R.id.collection_set_list_recycler_view)

        searchCardResultViewModel.expansionSetMap.observe(viewLifecycleOwner, {
            collectionViewModel.setExpansionSetMap(it)
        })

        collectionViewModel.expansionSetDisplayMap.observe(viewLifecycleOwner, {
            (setRecyclerView.adapter as SetRecyclerAdapter).notifyDataSetChangedAndUpdate()
        })

        val linearLayoutManager = LinearLayoutManager(context)
        setRecyclerView.adapter =
            SetRecyclerAdapter(requireContext(), collectionViewModel.expansionSetDisplayMap.value!!, {
                val expansionSet =
                    (setRecyclerView.adapter as SetRecyclerAdapter).getList()[setRecyclerView.getChildLayoutPosition(it)]
                searchCardResultViewModel.filterCardsBySet(expansionSet.setCode)?.let { printingList ->
                    collectionViewModel.setPrintingList(printingList)
                    collectionViewModel.setCurrentSetCollectionEntryMap(expansionSet.setCode)
                }

                val action =
                    CollectionFragmentDirections.actionNavigationCollectionToNavigationCollectionSingleSet(expansionSet.name, expansionSet.setCode)
                navController.navigate(action)
            }, {
                //This is so hacky. I can't believe this worked.
                val expansionSet =
                    ((setRecyclerView.findContainingViewHolder(it)!!.itemView.child_set_recycler_view).adapter as SetChildRecyclerAdapter).getList()[setRecyclerView.getChildLayoutPosition(
                        it
                    )]
                searchCardResultViewModel.filterCardsBySet(expansionSet.setCode)?.let { printingList ->
                    collectionViewModel.setPrintingList(printingList)
                    collectionViewModel.setCurrentSetCollectionEntryMap(expansionSet.setCode)
                }

                val action =
                    CollectionFragmentDirections.actionNavigationCollectionToNavigationCollectionSingleSet(expansionSet.name, expansionSet.setCode)
                navController.navigate(action)

            }, {
                val position = setRecyclerView.getChildLayoutPosition(it)
                (setRecyclerView.adapter as SetRecyclerAdapter).setExpandedPosition(position)
            })
        setRecyclerView.layoutManager = linearLayoutManager
    }
}
