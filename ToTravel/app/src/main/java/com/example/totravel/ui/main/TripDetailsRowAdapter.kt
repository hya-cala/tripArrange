package com.example.totravel.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.totravel.R
import com.example.totravel.Tools.DateTool.Companion.dateToString
import com.example.totravel.databinding.RowTripDetailsBinding
import com.example.totravel.glide.Glide
import com.example.totravel.model.DestinationMeta

class TripDetailsRowAdapter(private val viewModel: MainViewModel,
                            private val longClickListener: (tripID: String, tripDate: String, desPos: Int) ->
                            Unit)
    : ListAdapter<DestinationMeta, TripDetailsRowAdapter.VH>(TripDetailsDiff()){

    // ViewHolder pattern holds row binding
    inner class VH(val tripDetailRowBinding : RowTripDetailsBinding) :
        RecyclerView.ViewHolder(tripDetailRowBinding.root) {

        init {

            // Get the text view of the trip name
            val tripID = viewModel.getTitle()

            // Get the text view of the trip date
            val tripDate: TextView = tripDetailRowBinding.root.findViewById(R.id.startdate)

            // Set the onClickListener
            tripDetailRowBinding.root.setOnClickListener {
                longClickListener(tripID, tripDate.text as String, absoluteAdapterPosition)}

        }

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
        val destination = getItem(position)

        // Retrieve the current row binding
        val binding = holder.tripDetailRowBinding

        // Set the travel date
        binding.startdate.text = "${dateToString(destination.startDate!!.toDate())} - "
        binding.enddate.text = dateToString(destination.endDate!!.toDate())

        // Set the travel location
        binding.location.text = destination.destination

        // Set the travel notes
        if (destination.destination.isNullOrBlank()) {
            binding.imageTextUnion.visibility = View.GONE
        }
        else {
            binding.imageTextUnion.visibility = View.VISIBLE
            binding.notes.text = destination.description
        }


        // Check to make sure the entry is valid
        if (viewModel.validWeatherPosition(position)) {

            // Retrieve the current weather information
            val tripWeather = viewModel.getTripWeather()

            // Set the temperature
            binding.tvTemperature.text = tripWeather[position].main.temp.toString()

            // Set the weather icon
            val weatherIcon = tripWeather[position].weather[0].icon
            val weatherURL = "http://openweathermap.org/img/wn/$weatherIcon@2x.png"

            Glide.glideFetch(weatherURL, weatherURL, binding.ivWeatherCondition)

        }

    }

    // Check item identity
    class TripDetailsDiff : DiffUtil.ItemCallback<DestinationMeta>() {
        // Item identity
        override fun areItemsTheSame(oldItem: DestinationMeta, newItem: DestinationMeta): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: DestinationMeta, newItem: DestinationMeta): Boolean {
            return oldItem.startDate == newItem.startDate
                    && oldItem.destination == newItem.destination
                    && oldItem.endDate == newItem.endDate
        }
    }

}