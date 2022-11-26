package com.example.totravel.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.totravel.MainActivity
import com.example.totravel.R
import com.example.totravel.databinding.FragmentRvBinding

class MainFragment : Fragment() {

    // Initialize the view model
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentRvBinding? = null
    private val binding get() = _binding!!

    // A private variable for the adapter
    private lateinit var adapter: TripSummaryRowAdapter

    companion object {
        fun newInstance() = MainFragment()
    }

    // Display the support action bar
    private fun setDisplayHomeAsEnabled(value : Boolean) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }

    // Set up the adapter
    private fun initAdapter(binding: FragmentRvBinding) : TripSummaryRowAdapter {

        viewModel.fetchTrips()

        // Initialize the adapter
        adapter = TripSummaryRowAdapter(viewModel) {tripName, tripDate, position ->

            // Create a new bundle
            val bundle = Bundle()

            // Create a trip ID
            val tripID = "$tripName - $tripDate"

            // Insert the trip ID and the position
            bundle.putString("TripID", tripID)
            bundle.putInt("position", position)

            // Create a new trip detail fragment
            val tripDetailFrag = TripDetailView.newInstance()

            // Set the arguments
            tripDetailFrag.arguments = bundle

            // Launch the new fragment
            parentFragmentManager.beginTransaction().replace(R.id.main_frame, tripDetailFrag)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()

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
                    viewModel.removeTripSummaryAt(position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyWhenFragmentForegrounded(tripSummaryRowAdapter: TripSummaryRowAdapter) {

        // When returning to the fragment, notify the changes to pick up modifications
        tripSummaryRowAdapter.notifyDataSetChanged()

    }

    // Set onClickListener on the floating action button
    private fun setFloatingActionButton() {

        binding.fab.setOnClickListener {

            // Launch an instance to update the trip summary
            parentFragmentManager.commit {
                add(R.id.main_frame, TripSummaryEdit.newInstance())
                addToBackStack(null)
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Set the title to trip summary
        viewModel.setTitle("ToTravel")

        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Set up the layout manager
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)

        // Set up the adapter
        val itemDecor = DividerItemDecoration(binding.recyclerView.context,
            LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecor)
        val adapter = initAdapter(_binding!!)
        binding.recyclerView.adapter = adapter

        // Set up the floating action button
        setFloatingActionButton()

        // Swipe left to delete
        initTouchHelper().attachToRecyclerView(binding.recyclerView)

        // Let the view model observe changes in the trip summaries
        viewModel.observeTripList().observe(viewLifecycleOwner) {

            // Add the trip summaries to the adapter
            adapter.submitList(it)

            // Notify trip summary changes
            adapter.notifyDataSetChanged()

        }

        // Notify changes
        notifyWhenFragmentForegrounded(adapter)

    }

    override fun onResume() {

        super.onResume()

        // Redraw the fragment when resuming
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        ft.detach(this).attach(this).commit()

        // Disable the back button
        setDisplayHomeAsEnabled(false)

    }


}