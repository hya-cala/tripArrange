package com.example.totravel.database

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.example.totravel.model.DestinationMeta
import com.example.totravel.model.TripMeta
import com.example.totravel.ui.main.MainViewModel
import com.example.totravel.ui.main.TripWithDest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.photolist.FirestoreAuthLiveData
import com.google.firebase.Timestamp

class dbHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val tripCollection = "allTrips"
    private val destinationCollection = "allDestinations"
    private val firebaseAuthLiveData = FirestoreAuthLiveData()

    fun fetchTripMeta(tripList: MutableLiveData<List<TripWithDest>>, refreshDone: MutableLiveData<Boolean>) {
        dbFetchTripMeta(tripList, refreshDone)
    }

    fun fetchDestinationMeta(position: Int, tripList: MutableLiveData<List<TripWithDest>>, refreshDone: MutableLiveData<Boolean>) {
        dbFetchDestinationMeta(position, tripList, refreshDone)
    }

    private fun dbFetchTripMeta(tripList: MutableLiveData<List<TripWithDest>>, refreshDone: MutableLiveData<Boolean>) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        limitAndGetTrip(
            db.collection(tripCollection)
                .document(currentUser.uid)
                .collection("Trips")
                .orderBy("startDate"),
            tripList,
            refreshDone,
        )
    }

    private fun dbFetchDestinationMeta(position: Int,tripList: MutableLiveData<List<TripWithDest>>, refreshDone: MutableLiveData<Boolean>) {
        val tripWithDest = tripList.value!![position]
        limitAndGetDestination(
            db.collection(destinationCollection)
                .document(tripWithDest.tripMeta.firestoreID)
                .collection("Destinations")
                .orderBy("startDate"),
            position,
            tripList,
            refreshDone
        )
    }

    private fun limitAndGetTrip(query: Query, tripList: MutableLiveData<List<TripWithDest>>, refreshDone: MutableLiveData<Boolean>) {
        query.limit(100).get()
            .addOnSuccessListener { result ->
                tripList.postValue(result.documents.mapNotNull {
                    it.toObject(TripMeta::class.java)?.let { it1 -> TripWithDest(it1,null) }
                })
                refreshDone.postValue(!refreshDone.value!!)
            }
            .addOnFailureListener {
                refreshDone.postValue(!refreshDone.value!!)
                Log.w(javaClass.simpleName, "Error when fetching trips", it)
            }
    }

    private fun limitAndGetDestination(query: Query, position: Int,tripList: MutableLiveData<List<TripWithDest>>, refreshDone: MutableLiveData<Boolean>) {
        query.limit(100).get()
            .addOnSuccessListener { result ->
                val newTrips = tripList.value!!.toMutableList()
                newTrips[position].destinations = result.documents.mapNotNull {
                    it.toObject(DestinationMeta::class.java)
                }.toMutableList()
                tripList.postValue(newTrips)
                refreshDone.postValue(!refreshDone.value!!)
            }
    }

    fun createTripMeta(
        tripMeta: TripMeta,
        tripList: MutableLiveData<List<TripWithDest>>,
        refreshDone: MutableLiveData<Boolean>
    ) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        db.collection(tripCollection)
            .document(currentUser.uid)
            .collection("Trips")
            .add(tripMeta)
            .addOnSuccessListener {
                dbFetchTripMeta(tripList, refreshDone)
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName,"Error when adding a trip", it)
            }
    }

    fun createDestinationMeta(
        destinationMeta: DestinationMeta,
        position: Int,
        tripLists: MutableLiveData<List<TripWithDest>>,
        refreshDone: MutableLiveData<Boolean>
    ) {
        val tripWithDest = tripLists.value?.get(position)
        db.collection(destinationCollection)
            .document(tripWithDest!!.tripMeta.firestoreID)
            .collection("Destinations")
            .add(destinationMeta)
            .addOnSuccessListener {
                dbFetchDestinationMeta(position, tripLists, refreshDone)
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName, "Error when adding a destination", it)
            }
    }

    fun removeTripMeta(
        tripMeta: TripMeta,
        tripList: MutableLiveData<List<TripWithDest>>,
        refreshDone: MutableLiveData<Boolean>
    ) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        db.collection(tripCollection)
            .document(currentUser.uid)
            .collection("Trips")
            .document(tripMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                println(tripMeta.firestoreID)
                db.collection(destinationCollection)
                    .document(tripMeta.firestoreID)
                    .delete()
                    .addOnFailureListener {
                        println("sfdjlk")
                        Log.w(javaClass.simpleName, "Error when removing related destinations", it)
                    }
                    .addOnSuccessListener {
                        Log.d(javaClass.simpleName, "DocumentSnapshot successfully deleted!")
                    }
                dbFetchTripMeta(tripList, refreshDone)
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName, "Error when removing a trip", it)
            }
    }

    fun removeDestinationMeta(
        destinationMeta: DestinationMeta,
        tripPosition: Int,
        tripList: MutableLiveData<List<TripWithDest>>,
        refreshDone: MutableLiveData<Boolean>
    ) {
        val tripWithDest = tripList.value?.get(tripPosition)
        db.collection(destinationCollection)
            .document(tripWithDest!!.tripMeta.firestoreID)
            .collection("Destinations")
            .document(destinationMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                dbFetchDestinationMeta(tripPosition, tripList, refreshDone)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Error when removing a destination", it)
            }

    }
    fun updateDestinationMeta(
        destinationID: String,
        description: String,
        destination: String,
        startDate: Timestamp,
        endDate: Timestamp,
        tripPosition: Int,
        tripList: MutableLiveData<List<TripWithDest>>,
        refreshDone: MutableLiveData<Boolean>
    ) {
        val tripWithDest = tripList.value?.get(tripPosition)
        db.collection(destinationCollection)
            .document(tripWithDest!!.tripMeta.firestoreID)
            .collection("Destinations")
            .document(destinationID)
            .update(
                "description", description,
                "destination", destination,
                "endDate", endDate,
                "startDate", startDate,
            )
            .addOnSuccessListener {
                dbFetchDestinationMeta(tripPosition, tripList, refreshDone)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Error when updating a destination", it)
            }
    }

}