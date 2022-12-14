package edu.utap.photolist

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.totravel.ui.main.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

// https://firebase.google.com/docs/auth/android/firebaseui
class AuthInit(viewModel: MainViewModel, signInLauncher: ActivityResultLauncher<Intent>) {
    companion object {
        private const val TAG = "AuthInit"
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            Log.d(TAG, "user null")
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

            // Set up a sign-in intent
            val signinIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()

            // Launch the sign-in intent
            signInLauncher.launch(signinIntent)

            // setIsSmartLockEnabled(false) solves some problems
        } else {
            Log.d(TAG, "user ${user.displayName} email ${user.email}")
            viewModel.updateUser()
        }
    }
}
