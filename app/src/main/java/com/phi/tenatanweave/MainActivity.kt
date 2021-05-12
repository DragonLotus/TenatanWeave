package com.phi.tenatanweave

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.phi.tenatanweave.data.*
import com.phi.tenatanweave.fragments.decklist.DeckListViewModel
import com.phi.tenatanweave.fragments.decks.DeckViewModel
import com.phi.tenatanweave.fragments.dialogfragments.*
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.fragments.singlecard.SingleCardViewModel

class MainActivity : AppCompatActivity(), DeckOptionsBottomSheetFragment.DeckOptionsItemClickListener, CardOptionsBottomSheetFragment.CardOptionsItemClickListener, CardPrintingsBottomSheetFragment.CardPrintingItemClickListener {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val searchCardResultViewModel: SearchCardResultViewModel by viewModels()
    private val singleCardViewModel: SingleCardViewModel by viewModels()
    private val deckViewModel: DeckViewModel by viewModels()
    private val deckListViewModel: DeckListViewModel by viewModels()

    private val cardValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            searchCardResultViewModel.cardMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                searchCardResultViewModel.cardMap.value?.set(data.key!!, data.getValue(BaseCard::class.java)!!)
            }
            searchCardResultViewModel.updateCardPrintingList()

        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val printingValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            searchCardResultViewModel.printingMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                searchCardResultViewModel.printingMap.value?.set(data.key!!, data.getValue(Printing::class.java)!!)
            }
            searchCardResultViewModel.updateCardPrintingList()
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val rulingValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            singleCardViewModel.rulingMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                val ruling = data.getValue(Ruling::class.java)!!
                singleCardViewModel.rulingMap.value?.set(
                    ruling.card.replace(".", ""),
                    data.getValue(Ruling::class.java)!!
                )
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val expansionSetValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            searchCardResultViewModel.setMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                searchCardResultViewModel.setMap.value?.set(
                    data.key!!,
                    data.getValue(ExpansionSet::class.java)!!
                )
            }
            searchCardResultViewModel.updateSets()
            searchCardResultViewModel.updateCardPrintingList()
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val formatValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            deckListViewModel.formatList.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                data.getValue(Format::class.java)?.let { deckListViewModel.formatList.value?.add(it) }
            }
            deckListViewModel.formatList.value?.let { deckViewModel.populateFormatList(it) }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val userValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.child("deckIdSequence").exists()) {
                deckViewModel.userDeckIdSequence = snapshot.child("deckIdSequence").getValue(Long::class.java)!!
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val deckValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            deckViewModel.userDeckList.value?.clear()
            if (snapshot.exists()) {
                snapshot.getValue(object : GenericTypeIndicator<List<Deck>>() {})?.let {
                    deckViewModel.setUserDeckList(
                        it
                    )
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    override fun onStart() {
        super.onStart()
        Firebase.database.reference.child(resources.getString(R.string.db_collection_cards))
            .addValueEventListener(cardValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_printings))
            .addValueEventListener(printingValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_rulings))
            .addValueEventListener(rulingValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_sets))
            .addValueEventListener(expansionSetValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_formats))
            .addValueEventListener(formatValueListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_deck, R.id.navigation_search
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        Firebase.database.reference.child(resources.getString(R.string.db_collection_cards))
            .removeEventListener(cardValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_printings))
            .removeEventListener(printingValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_printings))
            .removeEventListener(rulingValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_sets))
            .removeEventListener(expansionSetValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_formats))
            .removeEventListener(formatValueListener)
        Firebase.auth.currentUser?.let {
            Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(it.uid)
                .removeEventListener(userValueListener)
            Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(it.uid)
                .child(resources.getString(R.string.db_collection_decks))
                .removeEventListener(deckValueEventListener)
        }
        viewModelStore.clear()
    }

    fun setFirebaseUserListener(uid: String): Boolean {
        Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(uid)
            .addValueEventListener(userValueListener)
        Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(uid)
            .child(resources.getString(R.string.db_collection_decks))
            .addValueEventListener(deckValueEventListener)
        return true
    }

    override fun onItemClick(item: String, bundle: Bundle) {
        when (item) {
            getString(R.string.deck_options_edit) -> {
                DeckDetailsDialogFragment.newInstance(bundle).show(supportFragmentManager, "MainActivity")
            }
            getString(R.string.deck_options_delete) -> {
                DeleteConfirmationDialogFragment.newInstance(bundle).show(supportFragmentManager, "MainActivity")
            }
            getString(R.string.card_options_printings) -> {
                CardPrintingsBottomSheetFragment.newInstance(bundle).show(supportFragmentManager, "MainActivity")
            }
            else -> {
                //Handle data
            }
        }
    }
}
