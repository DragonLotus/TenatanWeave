package com.phi.tenatanweave

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
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
import com.phi.tenatanweave.fragments.collection.CollectionViewModel
import com.phi.tenatanweave.fragments.decks.DeckViewModel
import com.phi.tenatanweave.fragments.dialogfragments.*
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.fragments.singlecard.SingleCardViewModel
import com.phi.tenatanweave.viewpagers.decklistviewpager.DeckListViewModel

class MainActivity : AppCompatActivity(), DeckOptionsBottomSheetFragment.DeckOptionsItemClickListener,
    CardOptionsBottomSheetFragment.CardOptionsItemClickListener,
    CardPrintingsBottomSheetFragment.CardPrintingItemClickListener {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val searchCardResultViewModel: SearchCardResultViewModel by viewModels()
    private val singleCardViewModel: SingleCardViewModel by viewModels()
    private val deckViewModel: DeckViewModel by viewModels()
    private val deckListViewModel: DeckListViewModel by viewModels()
    private val collectionViewModel: CollectionViewModel by viewModels()

    private val cardValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            searchCardResultViewModel.cardMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                try {
                    searchCardResultViewModel.cardMap.value?.set(data.key!!, data.getValue(BaseCard::class.java)!!)
                } catch (e: Exception) {
                    data.key?.let { Log.d("MainActivity", "Card Data key is: $it") }
                    Log.d("Exception", e.stackTraceToString())
                }
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
                try {
                    searchCardResultViewModel.printingMap.value?.set(data.key!!, data.getValue(Printing::class.java)!!)
                } catch (e: Exception) {
                    data.key?.let { Log.d("MainActivity", "Printing Data key is: $it") }
                    Log.d("Exception", e.stackTraceToString())
                }
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
                try {
                    val ruling = data.getValue(Ruling::class.java)!!
                    singleCardViewModel.rulingMap.value?.set(
                        ruling.card.replace(".", ""),
                        data.getValue(Ruling::class.java)!!
                    )
                } catch (e: Exception) {
                    data.key?.let { Log.d("MainActivity", "Ruling Data key is: $it") }
                    Log.d("Exception", e.stackTraceToString())
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val expansionSetValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            searchCardResultViewModel.setMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                try {
                    searchCardResultViewModel.setMap.value?.set(
                        data.key!!,
                        data.getValue(ExpansionSet::class.java)!!
                    )
                } catch (e: Exception) {
                    data.key?.let { Log.d("MainActivity", "ExpansionSet Data key is: $it") }
                    Log.d("Exception", e.stackTraceToString())
                }
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
                try {
                    data.getValue(Format::class.java)?.let { deckListViewModel.formatList.value?.add(it) }
                } catch (e: Exception) {
                    data.key?.let { Log.d("MainActivity", "Format Data key is: $it") }
                    Log.d("Exception", e.stackTraceToString())
                }
            }
            deckListViewModel.formatList.value?.let { deckViewModel.populateFormatList(it) }
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }

    private val collectionValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                collectionViewModel.setFullUserCollection(snapshot.getValue(FullUserCollection::class.java)!!)
            } catch (e: Exception) {
                snapshot.key?.let { Log.d("MainActivity", "Collection Data key is: $it") }
                Log.d("Exception", e.stackTraceToString())
            }

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
                try {
                    snapshot.getValue(object : GenericTypeIndicator<List<Deck>>() {})?.let {
                        deckViewModel.setUserDeckList(
                            it
                        )
                    }
                } catch (e: Exception) {
                    snapshot.key?.let { Log.d("MainActivity", "Deck Data key is: $it") }
                    Log.d("Exception", e.stackTraceToString())
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
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_deck, R.id.navigation_search, R.id.navigation_collection
//            )
//        )
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
        Firebase.database.reference.child(resources.getString(R.string.db_collection_collection))
            .removeEventListener(collectionValueListener)
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
        val firebaseUserDirectory =
            Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(uid)
        firebaseUserDirectory
            .addValueEventListener(userValueListener)
        firebaseUserDirectory
            .child(resources.getString(R.string.db_collection_decks))
            .addValueEventListener(deckValueEventListener)
        firebaseUserDirectory
            .child(resources.getString(R.string.db_collection_collection))
            .addValueEventListener(collectionValueListener)
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
