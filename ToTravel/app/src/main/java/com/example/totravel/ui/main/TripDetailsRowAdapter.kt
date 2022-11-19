package com.example.totravel.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.totravel.databinding.RowTripDetailsBinding
import com.example.totravel.databinding.RowTripSummaryBinding

class TripDetailsRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<TripDetail, TripDetailsRowAdapter.VH>(TripDetailsDiff()){

    // ViewHolder pattern holds row binding
    inner class VH(val tripDetailRowBinding : RowTripDetailsBinding) :
        RecyclerView.ViewHolder(tripDetailRowBinding.root) {

        // Set up the binding
        val tripRowBinding = tripDetailRowBinding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        // Inflate the rowBinding
        val rowBinding = RowTripDetailsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        // Return the view holder
        return VH(rowBinding)

    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        // Retrieve the current trip information
        val tripDetail = getItem(position)

        // Retrieve the current row binding
        val binding = holder.tripRowBinding

        // Set the travel date
        binding.date.text = tripDetail.travelDate

        // Set the travel location
        binding.location.text = tripDetail.location

        // Set the travel notes
        binding.notes.text = tripDetail.tripNotes

    }

    // Check item identity
    class TripDetailsDiff : DiffUtil.ItemCallback<TripDetail>() {
        // Item identity
        override fun areItemsTheSame(oldItem: TripDetail, newItem: TripDetail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: TripDetail, newItem: TripDetail): Boolean {
            return oldItem.travelDate == newItem.travelDate
                    && oldItem.location == newItem.location
        }
    }

}