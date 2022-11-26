package com.example.totravel.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

data class DestinationMeta(
    var ownerUid: String = "",
    var tripUuid: String = "",
    var destination: String = "",
    var description: String = "",
    var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    @DocumentId var firestoreID: String = "",
)
