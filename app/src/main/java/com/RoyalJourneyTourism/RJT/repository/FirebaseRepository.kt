package com.RoyalJourneyTourism.RJT.repository

import android.util.Log
import com.RoyalJourneyTourism.RJT.data.Booking
import com.RoyalJourneyTourism.RJT.data.BookingDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val bookingDao: BookingDao) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun syncMissedRecords() {
        val missedRecords = bookingDao.getMissedRecords()
        if (missedRecords.isEmpty()) return

        Log.d("FirebaseSync", "Starting sync for missed records: $missedRecords")

        try {
            val chunks = missedRecords.chunked(500)
            chunks.forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { record ->
                    val docRef = firestore.collection(COLLECTION_BOOKINGS).document()
                    batch.set(docRef, record)
                }
                batch.commit().await()
            }

            missedRecords.forEach { record ->
                val updatedRecord = record.copy(firebaseSync = true)
                bookingDao.upsertRecord(updatedRecord)
            }

            Log.d("FirebaseSync", "All records synced successfully.")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed: ${e.message}")
            // Optional: Mark records for retry or trigger retry logic
        }
    }



    suspend fun syncNewRecord(booking: Booking) {
        try {
            firestore.collection(COLLECTION_BOOKINGS).add(booking).await()
            val updateRecord = booking.copy(firebaseSync = true)
            bookingDao.upsertRecord(updateRecord)
        } catch (e: Exception) {
            Log.e("FirebaseSync", "New record Sync failed: ${e.message}")
        }
    }

    companion object {
        const val COLLECTION_BOOKINGS = "Bookings"
    }
}