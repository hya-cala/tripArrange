package com.example.totravel.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.totravel.R
import com.example.totravel.databinding.TripDetailEditBinding

class TripDetailEdit : Fragment(R.layout.trip_detail_edit) {

    // A variable for the view model
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: TripDetailEditBinding? = null

    private val binding get() = _binding!!

    companion object {

        private const val tripIDKey = "tripID"

        fun newInstance() : TripDetailEdit {
            return TripDetailEdit()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        _binding = TripDetailEditBinding.bind(view)

        // Retrieve the trip ID
        val tripID = viewModel.getTitle()

        // Put cursor in edit text
        binding.inputETDate.requestFocus()

        // Set onClickListener on the save button
        binding.saveButton.setOnClickListener {

            // Retrieve the trip date
            val tripDate = binding.inputETDate.text.toString()

            // Retrieve the trip location
            val tripLocation = binding.inputETLocation.text.toString()

            // Retrieve the trip notes
            val tripNotes = binding.inputETNotes.text.toString()

            // Check if either date or location is missing
            if (tripDate.isEmpty() or tripLocation.isEmpty()) {

                // Send a note
                Toast.makeText(activity, "Enter trip detail info!", Toast.LENGTH_LONG).show()

            } else {

                // Create a trip day ID
                val tripDayID = "$tripID - $tripDate"

                // Create a trip detail object
                val newTripDetail = TripDetail(tripDate, tripLocation, tripNotes, tripID, tripDayID)

                // Check if want to update instead
                if (viewModel.getOldTripDetailID().isNotEmpty()) {

                    // Update the trip detail
                    viewModel.updateTripDetail(viewModel.getOldTripDetailID(), newTripDetail)

                    // Set the old trip detail to empty
                    viewModel.setOldTripDetailID("")

                } else {

                    // Save the trip summary entry
                    viewModel.addTripDetail(newTripDetail)

                }

            }

            // Exit the fragment
            parentFragmentManager.popBackStack()

        }

        // Set onClickListener on the cancel button
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

}