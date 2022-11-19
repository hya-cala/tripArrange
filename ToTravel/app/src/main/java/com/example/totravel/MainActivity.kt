package com.example.totravel

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.totravel.databinding.ActionBarBinding
import com.example.totravel.databinding.ActivityMainBinding
import com.example.totravel.ui.main.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import edu.utap.photolist.AuthInit

class MainActivity : AppCompatActivity() {

    // A variable for the action bar
    private var actionBarBinding: ActionBarBinding? = null

    // A value for the view model
    private val viewModel: MainViewModel by viewModels()

    // Launch the sign-in
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.updateUser()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    // Initialize the action bar
    private fun initActionBar(actionBar: ActionBar) {

        // Disable the default and enable the custom
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)

        // Apply the custom view
        actionBar.customView = actionBarBinding?.root

    }

    private fun initTitleObservers() {

        // Observe title changes
        viewModel.observeTitle().observe(this) { newTitle ->

            // Update the title of the support action bar
            actionBarBinding?.actionTitle?.text = newTitle

        }

    }

    // Add a home fragment
    private fun addHomeFragment() {

        // No back stack for home
        supportFragmentManager.commit {
            add(R.id.main_frame, MainFragment.newInstance())
            addToBackStack(null)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }

        // Initialize the sign-in process
        //AuthInit(viewModel, signInLauncher)

        // Observe title changes
        initTitleObservers()

        // Add the home fragment
        addHomeFragment()

    }
}