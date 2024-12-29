package com.Trip.Trip360.repository

import android.util.Log
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.data.BookingDao
import com.Trip.Trip360.data.FirebaseModel
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
                val docRef = firestore.collection(webName).add(record.toFirebaseModel()).await()
                val updatedRecord = record.copy(firebaseSync = true, firestoreDocRef = docRef.id)
                bookingDao.updateInvoice(updatedRecord)
            }

            Log.d("FirebaseSync", "All records synced successfully to collection: $webName.")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed: ${e.message}")
        }
    }

    suspend fun syncRecord(invoice: Invoice, webName: String) {
        try {
            val existingDocRef = invoice.firestoreDocRef

            Log.d("FirebaseSync", "Firebase doc ref: $existingDocRef")

            if (existingDocRef != null) {
                firestore.collection(webName)
                    .document(existingDocRef)
                    .set(invoice.toFirebaseModel())
                    .await()
            } else {
                val newDocumentRef = firestore.collection(webName)
                    .add(invoice.toFirebaseModel())
                    .await()

                val updateRecord = invoice.copy(firebaseSync = true, firestoreDocRef = newDocumentRef.id)
                Log.d("FirebaseSync", "Updated local record: $updateRecord")
                bookingDao.updateInvoice(updateRecord)
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Record sync failed for invoiceId: ${invoice}: ${e.message}")
        }
    }


    private fun Invoice.toFirebaseModel(): FirebaseModel {
        return FirebaseModel(
            name = this.name,
            packageName = this.packageName ?: "",
            totalPrice = this.totalPrice,
            timeStamp = this.currentDate,
        )
    }

}
