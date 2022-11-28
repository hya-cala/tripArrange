package com.example.totravel.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.totravel.R
import com.example.totravel.Tools.DateTool
import com.example.totravel.Tools.DateTool.Companion.dateToString
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
        binding.inputETLocation.requestFocus()

        if (viewModel.getCurrentDestinationPosition() != -1) {
            val curDestination = viewModel.getCurrentDestinationMeta()
            binding.inputETLocation.setText(curDestination.destination)
            binding.inputETDateEnd.setText(dateToString(curDestination.endDate!!.toDate()))
            binding.inputETDateStart.setText(dateToString(curDestination.startDate!!.toDate()))
            binding.inputETNotes.setText(curDestination.description)
        }

        binding.inputETLocation.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !binding.inputETNotes.hasFocus()) {
                val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        binding.inputETLocation.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !binding.inputETLocation.hasFocus()) {
                val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        binding.inputETDateStart.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.datepickerStart.visibility=View.VISIBLE
                if (!binding.inputETDateStart.text.isNullOrBlank()) {
                    val date = stringToDate(binding.inputETDateStart.text.toString())
                    if (date != null) {
                        binding.datepickerStart.init(date.year + 1900, date.month, date.date) {view, year, month, day ->
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
                    binding.inputETDateStart.setText(
                        dateToString(
                            Date(
                                current.year - 1900,
                                current.monthValue - 1,
                                current.dayOfMonth
                            )
                        )
                    )
                    binding.datepickerStart.init(current.year, current.monthValue - 1, current.dayOfMonth) {view, year, month, day ->
                        binding.inputETDateStart.setText(
                            dateToString(
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
                binding.datepickerStart.visibility = View.GONE
                binding.datepickerStart.clearFocus()
            }

        }

        binding.inputETDateEnd.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.datepickerEnd.visibility=View.VISIBLE
                if (!binding.inputETDateEnd.text.isNullOrBlank()) {
                    val date = DateTool.stringToDate(binding.inputETDateEnd.text.toString())
                    if (date != null) {
                        binding.datepickerEnd.init(date.year + 1900, date.month, date.date) {view, year, month, day ->
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
                    binding.inputETDateEnd.setText(
                        dateToString(
                            Date(
                                current.year - 1900,
                                current.monthValue - 1,
                                current.dayOfMonth
                            )
                        )
                    )
                    binding.datepickerEnd.init(current.year, current.monthValue - 1, current.dayOfMonth) {view, year, month, day ->
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
                binding.datepickerEnd.visibility = View.GONE
                binding.datepickerEnd.clearFocus()
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
                    val startDate = Timestamp(stringToDate(tripStartDate)!!)
                    val endDate = Timestamp(stringToDate(tripEndDate)!!)
                    // Save the trip summary entry
                    if (startDate > endDate) {
                        Toast.makeText(activity, "Please check your start/end time.", Toast.LENGTH_SHORT).show()
                    } else if (viewModel.getCurrentDestinationPosition() == -1) {
                        viewModel.addDestination(
                            tripPosition = tripPosition,
                            destination = binding.inputETLocation.text.toString(),
                            description = binding.inputETNotes.text.toString(),
                            startDate = startDate,
                            endDate = endDate,
                        )
                        // Exit the fragment
                        parentFragmentManager.popBackStack()
                        viewModel.setCurrentDestinationPosition(-1)
                    } else {
                        viewModel.updateDestination(
                            tripPosition=tripPosition,
                            destination = binding.inputETLocation.text.toString(),
                            description = binding.inputETNotes.text.toString(),
                            startDate = startDate,
                            endDate = endDate,
                        )
                        // Exit the fragment
                        parentFragmentManager.popBackStack()
                        viewModel.setCurrentDestinationPosition(-1)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Something wrong happens, please try later.", Toast.LENGTH_SHORT)
            }
        }

        // Set onClickListener on the cancel button
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            viewModel.setCurrentDestinationPosition(-1)
        }

    }

}