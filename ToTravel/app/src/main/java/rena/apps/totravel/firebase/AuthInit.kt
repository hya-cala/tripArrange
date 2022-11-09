package rena.apps.totravel.firebase

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import rena.apps.totravel.viewmodels.MainViewModel

class AuthInit(viewModel: MainViewModel, signInLauncher: ActivityResultLauncher<Intent> ){
    companion object {
        private const val TAG = "AuthInit"
        fun setDisplayName(displayName : String, viewModel: MainViewModel) {
            Log.d(TAG, "XXX profile change request")
            // XXX Write me. User is attempting to update display name. Get the profile updates (see android doc)
            val updateProfile = UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
            val user = FirebaseAuth.getInstance().currentUser
            user?.updateProfile(updateProfile)?.addOnCompleteListener {
                viewModel.updateUser()
            }
        }
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            Log.d(TAG, "XXX user null")
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(intent)
        } else {
            Log.d(TAG, "XXX user ${user.displayName} email ${user.email}")
            viewModel.updateUser()
        }
    }
}