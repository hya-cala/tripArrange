package com.example.totravel.ui.main

import androidx.lifecycle.*
import com.example.totravel.api.CurrentWeatherResponse
import com.example.totravel.api.Repository
import com.example.totravel.api.WeatherApi
import com.example.totravel.database.dbHelper
import com.example.totravel.model.DestinationMeta
import com.example.totravel.model.TripMeta
import edu.utap.photolist.FirestoreAuthLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp


data class TripInfo(val tripName: String, val tripDate: String, val tripID: String)
data class TripDetail(
    var travelDate: String, var location: String, var tripNotes: String,
    val tripID: String, val tripDayID: String)
data class TripLocation(val location: String, val tripID: String)

data class TripWithDest(val tripMeta: TripMeta, var destinations: MutableList<DestinationMeta>?)

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    companion object {
        val weatherAppID = "484377a76210f32f50c49ada34d7aa9c"
    }

    private val dbHelper = dbHelper()

    // Authentication data
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    // Initialize a Weather API object
    private val weatherAPI = WeatherApi.create()

    // Create a repository
    private val weatherRepository = Repository(weatherAPI)

    // Create a list of trip details
    private val tripDetailList = MediatorLiveData<MutableList<TripDetail>>()

    // Create a list of trip locations
    private val tripLocations = mutableListOf<TripLocation>()

    // Create a list of IDs for individual days within a trip
    private val tripDayIDKeys = mutableListOf<String>()

    // Create a variable for the weather information
    private val weatherInfo = MediatorLiveData<List<CurrentWeatherResponse>>()

    // Create a variable to store the title
    private var title = MutableLiveData<String>()

    private var tripLists = MutableLiveData<List<TripWithDest>>()

    private val currentTrip = MutableLiveData<Int>()

    private val currentDestinations = MediatorLiveData<MutableList<DestinationMeta>>()
    init {
        currentDestinations.addSource(
            tripLists
        ) {
            if (currentTrip.value != null && tripLists.value != null) {
                currentDestinations.postValue(
                    tripLists.value!![currentTrip.value!!].destinations ?: emptyList<DestinationMeta>().toMutableList()
                )
            }
        }
        currentDestinations.addSource(currentTrip) {
            if (currentTrip.value != null) {
                currentDestinations.postValue(
                    tripLists.value?.get(currentTrip.value!!)?.destinations ?: emptyList<DestinationMeta>().toMutableList()
                )
            }
        }
    }


    // Create a variable to store the ID for the trip detail to be updated
    private var oldTripDetailID = ""

    fun fetchTrips() {
        dbHelper.fetchTripMeta(tripLists)
    }

    fun fetchDestinations(position: Int) {
        if (tripLists.value == null || tripLists.value!![position].destinations != null) {
            return
        }
        dbHelper.fetchDestinationMeta(position, tripLists)
    }

    fun removeTrip(position: Int) {
        val trip = tripLists.value?.get(position)
        trip?.tripMeta?.let { dbHelper.removeTripMeta(it, tripLists) }
    }

    fun removeDestination(tripPosition: Int, destinationPosition: Int) {
        val trip = tripLists.value?.get(tripPosition)
        val destination = trip?.destinations?.get(destinationPosition)
        if (destination != null) {
            dbHelper.removeDestinationMeta(destination,tripPosition, tripLists)
        }
    }

    fun addTrip(tripName: String, description: String, startDate: Timestamp, endDate: Timestamp) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        val tripMeta = TripMeta(
            ownerUid = currentUser.uid,
            tripName = tripName,
            description = description,
            startDate = startDate,
            endDate = endDate,
        )
        dbHelper.createTripMeta(tripMeta, tripLists)
    }

    fun addDestination(
        tripPosition: Int,
        destination: String,
        description: String,
        startDate: Timestamp,
        endDate: Timestamp,
    ) {
        val trip = tripLists.value?.get(tripPosition)
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        val destinationMeta = DestinationMeta(
            ownerUid = currentUser.uid,
            tripUuid = trip!!.tripMeta!!.firestoreID,
            destination = destination,
            description = description,
            startDate = startDate,
            endDate = endDate,
        )
        dbHelper.createDestinationMeta(destinationMeta, tripPosition, tripLists)
    }

    fun getCurrentTripPosition(): Int {
        return currentTrip.value!!
    }

    fun updateCurrentTripPosition(position: Int) {
        currentTrip.postValue(position)
    }

    fun observeCurrentDestinations(): LiveData<MutableList<DestinationMeta>> {
        return currentDestinations
    }

    fun getTripMeta(position: Int): TripMeta {
        val trip = tripLists.value?.get(position)
        return trip!!.tripMeta!!
    }

    fun getDestinationMetaList(position: Int): List<DestinationMeta> {
        val trip = tripLists.value?.get(position)
        if (trip != null && trip.destinations == null) {
            dbHelper.fetchDestinationMeta(position, tripLists)
        }
        return trip?.destinations?.toList() ?: emptyList()
    }

    fun observeTripList(): LiveData<List<TripWithDest>> {
        return tripLists
    }

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