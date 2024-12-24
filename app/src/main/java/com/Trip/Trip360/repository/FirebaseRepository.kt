package com.Trip.Trip360.repository

import android.util.Log
import com.Trip.Trip360.data.Booking
import com.Trip.Trip360.data.BookingDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val bookingDao: BookingDao) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun syncMissedRecords(webName: String) {
        val missedRecords = bookingDao.getMissedRecordsForWebName(webName) // Scoped to webName
        if (missedRecords.isEmpty()) return

        Log.d("FirebaseSync", "Starting sync for missed records: $missedRecords")

        try {
            for (record in missedRecords) {
                // Check if the record already exists in Firestore
                val existingDocument = firestore.collection(webName)
                    .whereEqualTo("invoiceId", record.invoiceId)
                    .get()
                    .await()

                if (existingDocument.isEmpty) {
                    // Only upload if the record doesn't already exist in Firestore
                    val docRef = firestore.collection(webName).document()
                    docRef.set(record).await()

                    // Mark the record as synced in Room
                    val updatedRecord = record.copy(firebaseSync = true)
                    bookingDao.upsertRecord(updatedRecord)
                } else {
                    Log.d("FirebaseSync", "Invoice already exists in Firestore: ${record.invoiceId}")
                }
            }

            Log.d("FirebaseSync", "All records synced successfully to collection: $webName.")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed: ${e.message}")
        }
    }


    suspend fun syncNewRecord(booking: Booking, webName: String) {
        try {
            firestore.collection(webName).add(booking).await()
            val updateRecord = booking.copy(firebaseSync = true)
            bookingDao.upsertRecord(updateRecord)
            Log.d("FirebaseSync", "New record synced successfully to collection: $webName.")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "New record sync failed: ${e.message}")
        }
    }
}
