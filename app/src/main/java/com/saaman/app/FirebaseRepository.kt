package com.saaman.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

/**
 * Single shared Firestore collection "entries".
 * Every device that opens the app reads/writes the SAME collection,
 * so any change made by one person is pushed in real time to everyone else's app.
 */
object FirebaseRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val COLLECTION = "entries"

    fun addEntry(entry: Entry, onComplete: (Boolean) -> Unit) {
        val doc = db.collection(COLLECTION).document()
        entry.id = doc.id
        entry.timestamp = System.currentTimeMillis()
        doc.set(entry)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteEntry(entryId: String, onComplete: (Boolean) -> Unit) {
        db.collection(COLLECTION).document(entryId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Listens for real-time changes. Fires immediately with cached/current data,
     * then again automatically whenever ANY device adds, edits, or deletes an entry.
     */
    fun listenToEntries(onChange: (List<Entry>) -> Unit): ListenerRegistration {
        return db.collection(COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Entry::class.java)?.apply { id = doc.id }
                }
                onChange(list)
            }
    }
}
