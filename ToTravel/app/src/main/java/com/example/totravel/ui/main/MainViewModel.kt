package com.example.totravel.ui.main

import androidx.lifecycle.*
import com.example.totravel.api.CurrentWeatherResponse
import com.example.totravel.api.Repository
import com.example.totravel.api.WeatherApi
import edu.utap.photolist.FirestoreAuthLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class TripInfo(val tripName: String, val tripDate: String, val tripID: String)
data class TripDetail(
    var travelDate: String, var location: String, var tripNotes: String,
    val tripID: String, val tripDayID: String)
data class TripLocation(val location: String, val tripID: String)

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    companion object {
        val weatherAppID = "484377a76210f32f50c49ada34d7aa9c"
    }

    // Authentication data
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    // Initialize a Weather API object
    private val weatherAPI = WeatherApi.create()

    // Create a repository
    private val weatherRepository = Repository(weatherAPI)

    // Create a list of trip summaries
    private val tripSummaryList = MediatorLiveData<MutableList<TripInfo>>()

    // Create a list of trip details
    private val tripDetailList = MediatorLiveData<MutableList<TripDetail>>()

    // Create a list of trip locations
    private val tripLocations = mutableListOf<TripLocation>()

    // Create a list of trip ID
    private val tripIDKeys = mutableListOf<String>()

    // Create a list of IDs for individual days within a trip
    private val tripDayIDKeys = mutableListOf<String>()

    // Create a variable for the weather information
    private val weatherInfo = MediatorLiveData<List<CurrentWeatherResponse>>()

    // Create a variable to store the title
    private var title = MutableLiveData<String>()

    // Create a variable to store the ID for the trip detail to be updated
    private var oldTripDetailID = ""

    // Observe changes in the title
    fun observeTitle(): LiveData<String> {
        return title
    }

    // Set the title
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    // Get the title
    fun getTitle(): String {
        return title.value ?: ""
    }

    // Reset the title
    fun resetTitle() {
        title.value = "ToTravel"
    }

    // Set the old trip detail ID
    fun setOldTripDetailID(oldTripID: String) {
        oldTripDetailID = oldTripID
    }

    // Get the old trip detail ID
    fun getOldTripDetailID(): String {
        return oldTripDetailID
    }

    // Observe changes in the trip summary
    fun observeTripSummary(): MediatorLiveData<MutableList<TripInfo>> {

        // Return the result
        return tripSummaryList

    }

    // Observe changes in the trip details
    fun observeTripDetail(): MediatorLiveData<MutableList<TripDetail>> {

        // Return the result
        return tripDetailList

    }

    // Get the current trip summary list
    fun getTripSummary(): MutableList<TripInfo> {

        // Return the result
        return tripSummaryList.value ?: mutableListOf()

    }

    // Add to the trip summary
    fun addTripSummary(newTripSummary: TripInfo) {

        // Retrieve the current trip summary list
        val currentTripSummaries = getTripSummary()

        // Add the new trip summary
        currentTripSummaries.add(newTripSummary)

        // Update the value
        tripSummaryList.postValue(currentTripSummaries)

        // Add the key of the new trip summary
        tripIDKeys.add(newTripSummary.tripID)

    }

    // Remove a trip summary from the list
    fun removeTripSummaryAt(currentTripSummaryPosition: Int) {

        // Remove the key of current trip summary from the key list
        tripIDKeys.removeAt(currentTripSummaryPosition)

        // Retrieve the current trip summary list
        val currentTripSummaryList = getTripSummary()

        // Remove the current trip summary from the list
        currentTripSummaryList.removeAt(currentTripSummaryPosition)

        // Update the value
        tripSummaryList.postValue(currentTripSummaryList)

    }

    // Get the current trip detail list
    fun getTripDetail(): MutableList<TripDetail> {

        // Return the result
        return tripDetailList.value ?: mutableListOf()

    }

    // Get the trip detail matching a given ID
    fun getTripDetailByID(tripDetailID: String): List<TripDetail> {

        // Get the current list of trip details
        val fullTripDetailList = getTripDetail()

        // Get a list of trip details that matches the given ID
        return fullTripDetailList.filter{it.tripID == tripDetailID}

    }

    // Add to the trip detail
    fun addTripDetail(newTripDetail: TripDetail) {

        // Retrieve the current trip detail list
        val currentTripDetails = getTripDetail()

        // Add the new trip detail
        currentTripDetails.add(newTripDetail)

        // Update the value
        tripDetailList.postValue(currentTripDetails)

        // Generate a new trip location
        val newTripLocation = TripLocation(newTripDetail.location, newTripDetail.tripID)

        // Add the new trip location
        tripLocations.add(newTripLocation)

        // Add the key of the new trip detail
        tripDayIDKeys.add(newTripDetail.tripDayID)

    }

    // Remove a trip detail from the list
    fun removeTripDetailAt(currentTripDetailPosition: Int) {

        // Remove the key of the current trip detail from the key list
        tripDayIDKeys.removeAt(currentTripDetailPosition)

        // Remove the location of the current trip detail from the list
        tripLocations.removeAt(currentTripDetailPosition)

        // Retrieve the current trip detail list
        val currentTripDetailList = getTripDetail()

        // Remove current trip detail from the list
        currentTripDetailList.removeAt(currentTripDetailPosition)

        // Update the value
        tripDetailList.postValue(currentTripDetailList)

    }

    // Update trip detail
    fun updateTripDetail(tripDetailID: String, tripDetail: TripDetail) {

        // Find the index of the trip detail to update
        val tripDetailIndex = tripDayIDKeys.indexOf(tripDetailID)

        // Retrieve the current trip detail list
        val currentTripDetails = getTripDetail()

        // Update the trip detail
        currentTripDetails[tripDetailIndex].travelDate = tripDetail.travelDate
        currentTripDetails[tripDetailIndex].location = tripDetail.location
        currentTripDetails[tripDetailIndex].tripNotes = tripDetail.tripNotes

        // Update the value
        tripDetailList.postValue(currentTripDetails)

        // Update the trip day ID list
        tripDayIDKeys[tripDetailIndex] = "${tripDetail.tripID} - ${tripDetail.travelDate}"

        // Update the location list
        tripLocations[tripDetailIndex] = TripLocation(tripDetail.location, tripDetail.tripID)

    }

    // Get the trip locations matching a given ID
    private fun getTripLocationByID(tripDetailID: String): List<String> {

        // Get a list of trip locations that matches the given ID
        val tripLocations = tripLocations.filter { it.tripID == tripDetailID }

        // Return a list of locations
        return tripLocations.map { it.location }

    }

    fun weatherRefresh(tripDetailID: String) {

        // Get a list of trip locations matching the given ID
        val locations = getTripLocationByID(tripDetailID)

        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO) {

            // Retrieve the weather information corresponding to each location
            weatherInfo.postValue(locations.map{ weatherRepository.fetchWeather(it, weatherAppID)})

        }

    }

    // Check the position is valid
    fun validWeatherPosition(position: Int): Boolean {

        return (position >= 0) and (position < weatherInfo.value!!.size)

    }

    // Get the current trip weather list
    fun getTripWeather(): List<CurrentWeatherResponse> {

        // Return the result
        return weatherInfo.value!!

    }

    // Observe changes in the trip weathers
    fun observeTripWeather(): MediatorLiveData<List<CurrentWeatherResponse>> {

        // Return the result
        return weatherInfo

    }

    // Update the current signed-in user
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }

}