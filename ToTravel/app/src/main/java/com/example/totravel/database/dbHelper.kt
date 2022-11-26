package com.example.totravel.database

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.totravel.model.DestinationMeta
import com.example.totravel.model.TripMeta
import com.example.totravel.ui.main.TripWithDest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class dbHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val tripCollection = "allTrips"
    private val destinationCollection = "allDestinations"

    fun fetchTripMeta(tripList: MutableLiveData<List<TripWithDest>> ) {
        dbFetchTripMeta(tripList)
    }

    fun fetchDestinationMeta(tripWithDest: TripWithDest) {
        dbFetchDestinationMeta(tripWithDest)
    }

    private fun dbFetchTripMeta(tripList: MutableLiveData<List<TripWithDest>>) {
        limitAndGetTrip(db.collection(tripCollection), tripList)
    }

    private fun dbFetchDestinationMeta(tripWithDest: TripWithDest) {
        limitAndGetDestination(db.collection(destinationCollection), tripWithDest)
    }

    private fun limitAndGetTrip(query: Query, tripList: MutableLiveData<List<TripWithDest>>) {
        query.limit(100).get()
            .addOnSuccessListener { result ->
                tripList.postValue(result.documents.mapNotNull {
                    it.toObject(TripMeta::class.java)?.let { it1 -> TripWithDest(it1,null) }
                })
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName, "Error when fetching trips", it)
            }
    }

    private fun limitAndGetDestination(query: Query, tripWithDest: TripWithDest) {
        query.limit(100).get()
            .addOnSuccessListener { result ->
                tripWithDest.destinations = result.documents.mapNotNull {
                    it.toObject(DestinationMeta::class.java)
                }.toMutableList()
            }
    }

    fun createTripMeta(
        tripMeta: TripMeta,
        tripList: MutableLiveData<List<TripWithDest>>
    ) {
        db.collection(tripCollection)
            .add(tripMeta)
            .addOnSuccessListener {
                dbFetchTripMeta(tripList)
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName,"Error when adding a trip", it)
            }
    }

    fun createDestinationMeta(
        destinationMeta: DestinationMeta,
        tripWithDest: TripWithDest
    ) {
        db.collection(destinationCollection)
            .add(destinationMeta)
            .addOnSuccessListener {
                dbFetchDestinationMeta(tripWithDest)
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName, "Error when adding a destination", it)
            }
    }

    fun removeTripMeta(
        tripMeta: TripMeta,
        tripList: MutableLiveData<List<TripWithDest>>
    ) {
        db.collection(tripCollection)
            .document(tripMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                dbFetchTripMeta(tripList)
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName, "Error when removing a trip", it)
            }
    }

    fun removeDestinationMeta(
        destinationMeta: DestinationMeta,
        tripWithDest: TripWithDest
    ) {
        db.collection(destinationCollection)
            .document(destinationMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                dbFetchDestinationMeta(tripWithDest)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Error when removing a destination", it)
            }

    }

}