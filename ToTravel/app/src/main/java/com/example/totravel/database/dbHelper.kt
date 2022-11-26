package com.example.totravel.database

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.totravel.model.DestinationMeta
import com.example.totravel.model.TripMeta
import com.example.totravel.ui.main.TripWithDest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.photolist.FirestoreAuthLiveData

class dbHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val tripCollection = "allTrips"
    private val destinationCollection = "allDestinations"
    private val firebaseAuthLiveData = FirestoreAuthLiveData()

    fun fetchTripMeta(tripList: MutableLiveData<List<TripWithDest>>) {
        dbFetchTripMeta(tripList)
    }

    fun fetchDestinationMeta(tripWithDest: TripWithDest) {
        dbFetchDestinationMeta(tripWithDest)
    }

    private fun dbFetchTripMeta(tripList: MutableLiveData<List<TripWithDest>>) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        limitAndGetTrip(
            db.collection(tripCollection)
                .document(currentUser.uid)
                .collection("Trips")
                .orderBy("startDate"),
            tripList)
    }

    private fun dbFetchDestinationMeta(tripWithDest: TripWithDest) {
        limitAndGetDestination(
            db.collection(destinationCollection)
                .document(tripWithDest.tripMeta.firestoreID)
                .collection("Destinations")
                .orderBy("startDate"),
            tripWithDest)
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
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        db.collection(tripCollection)
            .document(currentUser.uid)
            .collection("Trips")
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
            .document(tripWithDest.tripMeta.firestoreID)
            .collection("Destinations")
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
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        db.collection(tripCollection)
            .document(currentUser.uid)
            .collection("Trips")
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
            .document(tripWithDest.tripMeta.firestoreID)
            .collection("Destinations")
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