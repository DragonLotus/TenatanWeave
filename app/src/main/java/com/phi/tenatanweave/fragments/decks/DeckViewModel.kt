package com.phi.tenatanweave.fragments.decks

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.Deck
import com.phi.tenatanweave.data.Format

class DeckViewModel : ViewModel() {

    private val mFormatList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }
    val formatList: LiveData<MutableList<String>> = mFormatList
    val formatSelection = MutableLiveData<Int>()

    private val _text = MutableLiveData<String>().apply {
        value = "Something about decks goes here"
    }
    val text: LiveData<String> = _text

    private val mLoggedInUser = MutableLiveData<FirebaseUser>().apply {
        value = null
    }
    val loggedInUser: LiveData<FirebaseUser> = mLoggedInUser

    private val mDatabaseDirectory = MutableLiveData<DatabaseReference>().apply {
        value = null
    }
    val databaseDirectory: LiveData<DatabaseReference> = mDatabaseDirectory

    private val mUserDeckList = MutableLiveData<MutableList<Deck>>().apply {
        value = mutableListOf()
    }
    val userDeckList: LiveData<MutableList<Deck>> = mUserDeckList

    var userDeckIdSequence: Long = 0

    fun setUserDeckList(value: List<Deck>) {
        userDeckList.value?.clear()
        userDeckList.value?.addAll(value)
//        userDeckList.value?.removeAll { it == null }
        mUserDeckList.notifyObserver()
    }

    fun setLoggedInUser(user: FirebaseUser) {
        mLoggedInUser.value = user
    }

    fun setDatabaseDirectory(db: DatabaseReference) {
        mDatabaseDirectory.value = db
    }

    fun refreshDeckList(resources: Resources): LiveData<MutableList<Deck>> {
        if (userDeckList.value == null || userDeckList.value?.size == 0) {
            val user = Firebase.auth.currentUser
            user?.uid?.let {
                Firebase.database.reference.child(resources.getString(R.string.db_collection_users)).child(it)
                    .child(resources.getString(R.string.db_collection_decks))
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userDeckList.value?.clear()
                            if (snapshot.exists()) {
                                try {
                                    snapshot.getValue(object : GenericTypeIndicator<List<Deck>>() {})?.let { deckList ->
                                        setUserDeckList(
                                            deckList
                                        )
                                    }
                                } catch (e: Exception) {
                                    snapshot.key?.let { key -> Log.d("MainActivity", "Deck Data key is: $key") }
                                    Log.d("Exception", e.stackTraceToString())
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
            }
        }
        return userDeckList
    }

    fun addDeck(deckName: String, deckFormat: String, resources: Resources) {
        mUserDeckList.value?.add(Deck(id = userDeckIdSequence.toString(), deckName = deckName, format = deckFormat))
        //Uhhhhhh
        userDeckIdSequence++
        databaseDirectory.value?.child(resources.getString(R.string.db_collection_deckIdSequence))
            ?.setValue(userDeckIdSequence)
        databaseDirectory.value?.child(resources.getString(R.string.db_collection_decks))?.setValue(mUserDeckList.value)
    }

    fun updateOrAddDeck(deck: Deck, resources: Resources) {
        val index = mUserDeckList.value?.indexOfFirst { it.id == deck.id }
        if (index != null && index >= 0) {
            mUserDeckList.value?.set(index, deck)
        } else {
            mUserDeckList.value?.add(deck)
        }

        //Am I really just sending the whole deck list over and over again?
        //AM I DUMB?
        databaseDirectory.value?.child(resources.getString(R.string.db_collection_decks))?.setValue(mUserDeckList.value)
    }

    fun deleteDeck(deck: Deck, resources: Resources) {
        mUserDeckList.value?.removeIf { it.id == deck.id }
        mUserDeckList.notifyObserver()

        databaseDirectory.value?.child(resources.getString(R.string.db_collection_decks))?.setValue(mUserDeckList.value)
    }

    fun populateFormatList(formatList: MutableList<Format>) {
        mFormatList.value?.clear()
        formatList.forEach { mFormatList.value?.add(it.name) }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}