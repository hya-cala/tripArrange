package com.example.totravel.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.totravel.R
import com.example.totravel.databinding.TripSummaryEditBinding
import com.google.firebase.Timestamp
import java.util.*

class TripSummaryEdit : Fragment(R.layout.trip_summary_edit) {

    // A variable for the view model
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: TripSummaryEditBinding? = null

    private val binding get() = _binding!!

    companion object {

        fun newInstance() : TripSummaryEdit {
            return TripSummaryEdit()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        _binding = TripSummaryEditBinding.bind(view)

        // Put cursor in edit text
        binding.inputETTripName.requestFocus()

        // Set onClickListener on the save button
        binding.saveButton.setOnClickListener {

            // Retrieve the trip name
            val tripName = binding.inputETTripName.text.toString()

            // Retrieve the trip date
            val tripDate = binding.inputETTripDate.text.toString()

            // Check if either info is missing
            if (tripName.isEmpty() or tripDate.isEmpty()) {

                // Send a note
                Toast.makeText(activity, "Enter trip summary info!", Toast.LENGTH_LONG).show()

            } else {

                // Create a trip ID
                val tripID = "$tripName - $tripDate"

                // Create a trip info object
                val newTripInfo = TripInfo(tripName, tripDate, tripID)

                // Save the trip summary entry
                // TODO: remove those hardcoded things
                val description = "test"
                val startDate = Timestamp(Date(2022, 1,1))
                val endDate = Timestamp(Date(2023, 1,1))
                viewModel.addTrip(
                    tripName,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                )

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