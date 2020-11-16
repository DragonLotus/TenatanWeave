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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.phi.tenatanweave.data.BaseCard
import com.phi.tenatanweave.data.ExpansionSet
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.Ruling
import com.phi.tenatanweave.fragments.searchcardresult.SearchCardResultViewModel
import com.phi.tenatanweave.fragments.singlecard.SingleCardViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val searchCardResultViewModel: SearchCardResultViewModel by viewModels()
    private val singleCardViewModel: SingleCardViewModel by viewModels()


    private val cardValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            searchCardResultViewModel.cardMap.value?.clear()
            for (data: DataSnapshot in snapshot.children) {
                searchCardResultViewModel.cardMap.value?.set(data.key!!, data.getValue(BaseCard::class.java)!!)
            }
            searchCardResultViewModel.updateCardPrintingList()

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
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
            TODO("Not yet implemented")
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
            TODO("Not yet implemented")
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
            TODO("Not yet implemented")
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
        Firebase.database.reference.child(R.string.db_collection_cards.toString())
            .removeEventListener(cardValueListener)
        Firebase.database.reference.child(R.string.db_collection_printings.toString())
            .removeEventListener(printingValueListener)
        Firebase.database.reference.child(R.string.db_collection_printings.toString())
            .removeEventListener(rulingValueListener)
        viewModelStore.clear()
    }
}
