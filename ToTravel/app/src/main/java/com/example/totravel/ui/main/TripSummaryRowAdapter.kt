package com.example.totravel.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.totravel.R
import com.example.totravel.Tools.DateTool
import com.example.totravel.databinding.RowTripSummaryBinding
import com.example.totravel.model.DestinationMeta

class TripSummaryRowAdapter(private val viewModel: MainViewModel,
                            private val clickListener: (tripName: String, tripDate: String, position: Int) -> Unit)
    : ListAdapter<TripWithDest, TripSummaryRowAdapter.VH>(TripSummaryDiff()){

    // A private variable for storing the trip ID
    private lateinit var tripID : String

    // ViewHolder pattern holds row binding
    inner class VH(val tripSummaryRowBinding : RowTripSummaryBinding) :
        RecyclerView.ViewHolder(tripSummaryRowBinding.root) {

        init {

            // Get the text view of the trip name
            val tripName: TextView = tripSummaryRowBinding.root.findViewById(R.id.tripName)

            // Get the text view of the trip date
            val tripDate: TextView = tripSummaryRowBinding.root.findViewById(R.id.date)

            // Set the onClickListener
            tripSummaryRowBinding.root.setOnClickListener {
                clickListener(tripName.text as String, tripDate.text as String, bindingAdapterPosition)}

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        // Inflate the rowBinding
        val rowBinding = RowTripSummaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        // Return the view holder
        return VH(rowBinding)

    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        // Retrieve the current trip information
        val trip = getItem(position)

        // Retrieve the current row binding
        val binding = holder.tripSummaryRowBinding

        // Set the trip name
        binding.tripName.text = trip.tripMeta.tripName

        // Set the trip date
        if (trip.tripMeta.startDate?.toDate() == null) {
            binding.date.text = ""
        } else {
            binding.date.text = DateTool.dateToString(
                trip.tripMeta.startDate?.toDate()!!)
        }


        // Store the current trip ID
        tripID = trip . tripMeta . firestoreID


    }

    // Check item identity
    class TripSummaryDiff : DiffUtil.ItemCallback<TripWithDest>() {
        // Item identity
        override fun areItemsTheSame(oldItem: TripWithDest, newItem: TripWithDest): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: TripWithDest, newItem: TripWithDest): Boolean {
            return oldItem.tripMeta.tripName == newItem.tripMeta.tripName
                    && oldItem.tripMeta.startDate == newItem.tripMeta.startDate
                    && oldItem.tripMeta.endDate == oldItem.tripMeta.endDate
        }
    }

}