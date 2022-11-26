package com.example.totravel.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.totravel.R
import com.example.totravel.Tools.DateTool
import com.example.totravel.Tools.DateTool.Companion.stringToDate
import com.example.totravel.databinding.TripDetailEditBinding
import com.example.totravel.model.DestinationMeta
import com.google.firebase.Timestamp
import edu.utap.photolist.FirestoreAuthLiveData
import java.time.LocalDate
import java.util.*

class TripDetailEdit : Fragment(R.layout.trip_detail_edit) {

    // A variable for the view model
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: TripDetailEditBinding? = null
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

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
        binding.inputETDateStart.requestFocus()

        binding.inputETDateStart.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.datepicker.visibility=View.VISIBLE
                if (!binding.inputETDateStart.text.isNullOrBlank()) {
                    val date = DateTool.stringToDate(binding.inputETDateStart.text.toString())
                    if (date != null) {
                        binding.datepicker.init(date.year + 1900, date.month, date.date) {view, year, month, day ->
                            binding.inputETDateStart.setText(
                                DateTool.dateToString(
                                    Date(
                                        year - 1900,
                                        month,
                                        day
                                    )
                                )
                            )
                        }
                    }
                }else {
                    val current = LocalDate.now()
                    binding.datepicker.init(current.year, current.dayOfMonth, current.dayOfYear) {view, year, month, day ->
                        binding.inputETDateStart.setText(
                            DateTool.dateToString(
                                Date(
                                    year - 1900,
                                    month,
                                    day
                                )
                            )
                        )
                    }
                }
            } else {
                binding.datepicker.visibility = View.GONE
                binding.datepicker.clearFocus()
            }

        }

        binding.inputETDateEnd.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.datepicker.visibility=View.VISIBLE
                if (!binding.inputETDateEnd.text.isNullOrBlank()) {
                    val date = DateTool.stringToDate(binding.inputETDateEnd.text.toString())
                    if (date != null) {
                        binding.datepicker.init(date.year + 1900, date.month, date.date) {view, year, month, day ->
                            binding.inputETDateEnd.setText(
                                DateTool.dateToString(
                                    Date(
                                        year - 1900,
                                        month,
                                        day
                                    )
                                )
                            )
                        }
                    }
                }else {
                    val current = LocalDate.now()
                    binding.datepicker.init(current.year, current.dayOfMonth, current.dayOfYear) {view, year, month, day ->
                        binding.inputETDateEnd.setText(
                            DateTool.dateToString(
                                Date(
                                    year - 1900,
                                    month,
                                    day
                                )
                            )
                        )
                    }
                }
            } else {
                binding.datepicker.visibility = View.GONE
                binding.datepicker.clearFocus()
            }

        }

        // Set onClickListener on the save button
        binding.saveButton.setOnClickListener {
            try {
                val currentUser = firebaseAuthLiveData.getCurrentUser()!!
                val tripPosition = viewModel.getCurrentTripPosition()

                // Retrieve the trip date
                val tripStartDate = binding.inputETDateStart.text.toString()
                val tripEndDate = binding.inputETDateEnd.text.toString()

                // Retrieve the trip location
                val tripLocation = binding.inputETLocation.text.toString()

                // Retrieve the trip notes
                val tripNotes = binding.inputETNotes.text.toString()

                // Check if either date or location is missing
                if (tripStartDate.isEmpty() || tripLocation.isEmpty() || tripEndDate.isEmpty()) {

                    // Send a note
                    Toast.makeText(activity, "Enter trip detail info!", Toast.LENGTH_LONG).show()

                } else {

                    // Create a trip day ID
                    val tripDayID = "$tripID - $tripStartDate"

                    // Save the trip summary entry
                    viewModel.addDestination(
                        tripPosition = tripPosition,
                        destination = binding.inputETLocation.text.toString(),
                        description = binding.inputETNotes.text.toString(),
                        startDate = Timestamp(stringToDate(tripStartDate)!!),
                        endDate = Timestamp(stringToDate(tripEndDate)!!),
                    )

                }

                // Exit the fragment
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Something wrong happens, please try later.", Toast.LENGTH_SHORT)
            }
        }

        // Set onClickListener on the cancel button
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

}