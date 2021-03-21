package com.phi.tenatanweave.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun setText(text: String) {
        _text.value = text
    }

    var isLoggedin: Boolean = false
    var isListenerAttached: Boolean = false

    private val mLoggedInUser = MutableLiveData<FirebaseUser>().apply {
        value = null
    }
    val loggedInUser: LiveData<FirebaseUser> = mLoggedInUser

    fun setLoggedInUser(user : FirebaseUser){
        mLoggedInUser.value = user
    }

}