package com.example.totravel.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.totravel.MainActivity
import com.example.totravel.R
import com.example.totravel.databinding.FragmentRvBinding
import kotlin.properties.Delegates

class TripDetailView : Fragment() {

    // Initialize the view model
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentRvBinding? = null
    private val binding get() = _binding!!
    private var position by Delegates.notNull<Int>()

    private lateinit var tripID : String

    // A private variable for the adapter
    private lateinit var adapter: TripDetailsRowAdapter

    companion object {
        fun newInstance(): TripDetailView {
            return TripDetailView()
        }
    }

    // Display the support action bar
    private fun setDisplayHomeAsUpEnabled(value : Boolean) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }

    // Set up the adapter
    private fun initAdapter(binding: FragmentRvBinding) : TripDetailsRowAdapter {
        position = requireArguments().getInt("position")

        viewModel.fetchDestinations(position)

        // Initialize the adapter
        adapter = TripDetailsRowAdapter(viewModel) {tripID, tripDate, DesPos ->

            // Create a tripDayID
            val oldTripDayID = "$tripID - $tripDate"

            // Set the ID for the old trip detail
            viewModel.setOldTripDetailID(oldTripDayID)

            viewModel.setCurrentDestinationPosition(DesPos)

            // Launch the trip detail edit
            parentFragmentManager.commit {
                add(R.id.main_frame, TripDetailEdit.newInstance())
                addToBackStack(null)
            }
            
        }

        // Return the adapter
        return adapter

    }

    // Gets the position of the selected item
    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    // Detecting swipes or moving items
    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = getPos(viewHolder)
                    Log.d(javaClass.simpleName, "Swipe delete $position")
                    viewModel.removeDestination(viewModel.getCurrentTripPosition(), position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }

    // Set onClickListener on the floating action button
    private fun setFloatingActionButton() {

        binding.fab.setOnClickListener {

            // Otherwise, launch an instance to update the trip detail
            parentFragmentManager.commit {
                add(R.id.main_frame, TripDetailEdit.newInstance())
                addToBackStack(null)
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstancesState: Bundle?
    ): View {

        // Retrieve the trip ID
        tripID = arguments?.getString("TripID", "")!!

        // Set the title to trip detail
        viewModel.setTitle(tripID)

        // Handle the behavior of the system back button
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                // Exit the current fragment
                requireActivity().supportFragmentManager.popBackStack()

            }

        })

        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstancesState: Bundle?) {

        super.onViewCreated(view, savedInstancesState)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Add to menu
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Menu is already inflated by main activity
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                // Change the title back to sub-Reddit
                viewModel.resetTitle()

                // Exit the current fragment
                requireActivity().supportFragmentManager.popBackStack()

                // Handle the menu selection
                return true

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Enable the back button
        setDisplayHomeAsUpEnabled(true)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchDestinations(position)
            viewModel.weatherRefresh()
        }

        viewModel.observeRefreshDone().observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Set up the floating action button
        setFloatingActionButton()

        // Swipe left to delete
        initTouchHelper().attachToRecyclerView(binding.recyclerView)

        // Set up the layout manager
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)

        // Set up the adapter
        adapter = initAdapter(_binding!!)
        binding.recyclerView.adapter = adapter

        // Update the weather information
        viewModel.weatherRefresh()

        // Notify the trip detail changes
        adapter.notifyDataSetChanged()

        // Let the view model observe changes
        viewModel.observeCurrentDestinations().observe(viewLifecycleOwner) {

            // Update the weather information
            viewModel.weatherRefresh()

            // Add to the adapter
            adapter.submitList(it.toList())

            // Notify the trip detail changes
            adapter.notifyDataSetChanged()

        }

        // Let the view model observe changes in the trip weather
        viewModel.observeTripWeather().observe(viewLifecycleOwner) {

            // Add to the adapter
            adapter.submitList(viewModel.observeCurrentDestinations().value)

            // Notify the trip detail changes
            adapter.notifyDataSetChanged()

        }

    }

    override fun onDestroyView() {
        _binding = null
        viewModel.clearCurrentDestinations()
        super.onDestroyView()
    }
}
