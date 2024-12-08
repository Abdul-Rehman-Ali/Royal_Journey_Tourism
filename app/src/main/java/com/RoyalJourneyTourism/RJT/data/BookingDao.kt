package com.RoyalJourneyTourism.RJT.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookingDao {
    @Upsert
    suspend fun upsertRecord(invoiceRecord: Booking)

    @Delete
    suspend fun deleteRecord(invoiceRecord: Booking)

    @Query("delete from Booking")
    suspend fun deleteAll()

    @Query("select * from Booking where firebaseSync = 0")
    suspend fun getMissedRecords() : List<Booking>
}