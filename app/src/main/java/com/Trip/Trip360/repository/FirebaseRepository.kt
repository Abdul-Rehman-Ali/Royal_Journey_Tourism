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
                    bookingDao.updateInvoice(updatedRecord)
                } else {
                    Log.d("FirebaseSync", "Invoice already exists in Firestore: ${record.invoiceId}")
                }
            }

            Log.d("FirebaseSync", "All records synced successfully to collection: $webName.")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed: ${e.message}")
        }
    }


    suspend fun syncRecord(invoice: Invoice, webName: String) {
        try {

            val querySnapshot = firestore.collection(webName)
                .whereEqualTo("invoiceId", invoice.invoiceId)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                val documentId = querySnapshot.documents.first().id
                firestore.collection(webName)
                    .document(documentId)
                    .set(invoice.toFirebaseModel())
                    .await()
                Log.d("FirebaseSync", "Record updated successfully for invoiceId: ${invoice.invoiceId}")
            } else {
                firestore.collection(webName)
                    .add(invoice.toFirebaseModel())
                    .await()
                Log.d("FirebaseSync", "New record inserted successfully for invoiceId: ${invoice.invoiceId}")
            }

            val updateRecord = invoice.copy(firebaseSync = true)
            bookingDao.updateInvoice(updateRecord)

        } catch (e: Exception) {
            Log.e("FirebaseSync", "Record sync failed for invoiceId: ${invoice.invoiceId}: ${e.message}")
        }
    }

    private fun Invoice.toFirebaseModel(): FirebaseModel {
        return FirebaseModel(
            name = this.name,
            packageName = this.packageName ?: "",
            totalPrice = this.totalPrice,
            currentDate = this.currentDate,
            invoiceId = this.invoiceId
        )
    }

}
