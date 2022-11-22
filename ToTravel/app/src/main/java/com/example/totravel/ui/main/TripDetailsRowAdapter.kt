package com.example.totravel.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.totravel.R
import com.example.totravel.databinding.RowTripDetailsBinding
import com.example.totravel.glide.Glide

class TripDetailsRowAdapter(private val viewModel: MainViewModel,
                            private val longClickListener: (tripID: String, tripDate: String) ->
                            Unit)
    : ListAdapter<TripDetail, TripDetailsRowAdapter.VH>(TripDetailsDiff()){

    // ViewHolder pattern holds row binding
    inner class VH(val tripDetailRowBinding : RowTripDetailsBinding) :
        RecyclerView.ViewHolder(tripDetailRowBinding.root) {

        init {

            // Get the text view of the trip name
            val tripID = viewModel.getTitle()

            // Get the text view of the trip date
            val tripDate: TextView = tripDetailRowBinding.root.findViewById(R.id.date)

            // Set the onClickListener
            tripDetailRowBinding.root.setOnClickListener {
                longClickListener(tripID, tripDate.text as String)}

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
        val tripDetail = getItem(position)

        // Retrieve the current row binding
        val binding = holder.tripDetailRowBinding

        // Set the travel date
        binding.date.text = tripDetail.travelDate

        // Set the travel location
        binding.location.text = tripDetail.location

        // Set the travel notes
        binding.notes.text = tripDetail.tripNotes

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