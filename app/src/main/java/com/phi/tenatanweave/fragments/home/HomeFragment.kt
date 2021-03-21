package com.phi.tenatanweave.fragments.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.phi.tenatanweave.MainActivity
import com.phi.tenatanweave.R


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private val RC_SIGN_IN = 9001
    private lateinit var signOutButton : Button
    private lateinit var signInButton: SignInButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireContext().getString(R.string.web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        signInButton = root.findViewById(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signOutButton = root.findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener{
            Firebase.auth.signOut()
            homeViewModel.setText("Not logged in.")
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }

        signInButton.setOnClickListener {
            signIn()
        }

//        val account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        val account = auth.currentUser
        if(account != null){
            homeViewModel.isLoggedin = true
            setLoggedIn(account)
        }
        return root
    }

    private fun setLoggedIn(user : FirebaseUser){
        user.displayName?.let {
            homeViewModel.setText("Welcome, ${if(it.contains(" ")) it.substring(0, it.indexOf(" ")) else user.displayName}.")
        }
        signInButton.visibility = View.GONE
        signOutButton.visibility = View.VISIBLE

        homeViewModel.setLoggedInUser(user)
        if(!homeViewModel.isListenerAttached){
            homeViewModel.isListenerAttached = (requireActivity() as MainActivity).setFirebaseUserListener(user.uid)
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask : Task<GoogleSignInAccount> ) {
        try{
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e : ApiException ){
            Log.w(this.tag, "signInResult: Failed code = " + e.statusCode)
            homeViewModel.setText("Something's fucky.")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    auth.currentUser?.let {
                        setLoggedIn(it)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                }

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.button_home).setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToHomeSecondFragment("From HomeFragment")
            NavHostFragment.findNavController(this@HomeFragment)
                .navigate(action)
        }
    }
}
