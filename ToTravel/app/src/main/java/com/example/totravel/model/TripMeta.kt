package com.example.totravel.model

import com.google.firebase.firestore.DocumentId
import java.sql.Timestamp

data class TripMeta(
    val tripUuid: String = "",
    val ownerUid: String = "",
    var tripName: String = "",
    var description: String = "",
    var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    @DocumentId var firestoreID: String = ""
)
