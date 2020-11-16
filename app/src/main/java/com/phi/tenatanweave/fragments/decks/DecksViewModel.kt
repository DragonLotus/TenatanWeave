package com.phi.tenatanweave.fragments.decks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DecksViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Something about decks goes here"
    }
    val text: LiveData<String> = _text
}