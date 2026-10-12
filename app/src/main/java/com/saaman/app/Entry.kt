package com.saaman.app

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

/**
 * Represents a single purchase entry.
 * date is stored as "yyyy-MM-dd" so entries sort correctly as strings too.
 * timestamp is used for ordering by "when it was added" (server time).
 */
data class Entry(
    @get:Exclude var id: String = "",
    var date: String = "",
    var person: String = "",
    var item: String = "",
    var amount: Double = 0.0,
    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Long = 0L
)

object People {
    val NAMES = listOf("Yashwanth", "Kishore", "Surya")
}
