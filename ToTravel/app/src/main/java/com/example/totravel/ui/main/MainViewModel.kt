package com.example.totravel.ui.main

import androidx.lifecycle.*
import edu.utap.photolist.FirestoreAuthLiveData


data class TripInfo(val tripName: String, val tripDate: String, val tripID: String)
data class TripDetail(val travelDate: String, val location: String, val tripNotes: String,
                      val tripID: String, val tripDayID: String)

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    // Authentication data
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    // Create a list of trip summaries
    private val tripSummaryList = MediatorLiveData<MutableList<TripInfo>>()

    // Create a list of trip details
    private val tripDetailList = MediatorLiveData<MutableList<TripDetail>>()

    // Create a list of trip ID
    private val tripIDKeys = mutableListOf<String>()

    // Create a list of IDs for individual days within a trip
    private val tripDayIDKeys = mutableListOf<String>()

    // Create a variable to store the title
    private var title = MutableLiveData<String>()

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

        // Find the index of the current trip summary
        // val index =tripIDKeys.indexOf(currentTripSummary.tripID)

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

        // Add the key of the new trip detail
        tripDayIDKeys.add(newTripDetail.tripDayID)

    }

    // Remove a trip detail from the list
    fun removeTripDetail(currentTripDetail: TripDetail) {

        // Find the index of the current trip detail
        val index = tripDayIDKeys.indexOf(currentTripDetail.tripDayID)

        // Remove the key of the current trip detail from the key list
        tripDayIDKeys.removeAt(index)

        // Retrieve the current trip detail list
        val currentTripDetailList = getTripDetail()

        // Remove current trip detail from the list
        currentTripDetailList.removeAt(index)

        // Update the value
        tripDetailList.postValue(currentTripDetailList)

    }

    // Update the current signed-in user
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }

}