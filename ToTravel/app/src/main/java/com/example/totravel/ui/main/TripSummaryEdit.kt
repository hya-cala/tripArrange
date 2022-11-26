package com.example.totravel.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.totravel.R
import com.example.totravel.Tools.DateTool
import com.example.totravel.Tools.DateTool.Companion.dateToString
import com.example.totravel.Tools.DateTool.Companion.stringToDate
import com.example.totravel.databinding.TripSummaryEditBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
        binding.inputETTripStartDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.datePicker.visibility=View.VISIBLE
                if (!binding.inputETTripStartDate.text.isNullOrBlank()) {
                    val date = stringToDate(binding.inputETTripStartDate.text.toString())
                    if (date != null) {
                        binding.datePicker.init(date.year + 1900, date.month, date.date) {view, year, month, day ->
                            binding.inputETTripStartDate.setText(dateToString(Date(year-1900, month, day)))
                        }
                    }
                }else {
                    val current = LocalDate.now()
                    binding.datePicker.init(current.year, current.dayOfMonth, current.dayOfYear) {view, year, month, day ->
                        binding.inputETTripStartDate.setText(dateToString(Date(year-1900, month, day)))
                    }
                }
            } else {
                binding.datePicker.visibility = View.GONE
                binding.datePicker.clearFocus()
            }

        }

        binding.inputETTripEndDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.datePicker.visibility=View.VISIBLE
                if (!binding.inputETTripEndDate.text.isNullOrBlank()) {
                    val date = stringToDate(binding.inputETTripEndDate.text.toString())
                    if (date != null) {
                        binding.datePicker.init(date.year + 1900, date.month, date.date) {view, year, month, day ->
                            binding.inputETTripEndDate.setText(dateToString(Date(year-1900, month, day)))
                        }
                    }
                }else {
                    val current = LocalDate.now()
                    binding.datePicker.init(current.year, current.dayOfMonth, current.dayOfYear) {view, year, month, day ->
                        binding.inputETTripEndDate.setText(dateToString(Date(year-1900, month, day)))
                    }
                }
            } else {
                binding.datePicker.visibility = View.GONE
                binding.datePicker.clearFocus()
            }

        }

        // Set onClickListener on the save button
        binding.saveButton.setOnClickListener {

            // Retrieve the trip name
            val tripName = binding.inputETTripName.text.toString()

            // Retrieve the trip date
            val tripDate = binding.inputETTripStartDate.text.toString()

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
                val startDate = stringToDate(binding.inputETTripStartDate.text.toString())
                val endDate = stringToDate(binding.inputETTripEndDate.text.toString())
                if (startDate == null || endDate == null) {
                    Toast.makeText(context, "Please enter all the required information!", Toast.LENGTH_SHORT)
                } else {
                    viewModel.addTrip(
                        tripName,
                        description = description,
                        startDate = Timestamp(startDate),
                        endDate = Timestamp(endDate),
                    )
                    // Exit the fragment
                    parentFragmentManager.popBackStack()
                }


            }

        }

        // Set onClickListener on the cancel button
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}