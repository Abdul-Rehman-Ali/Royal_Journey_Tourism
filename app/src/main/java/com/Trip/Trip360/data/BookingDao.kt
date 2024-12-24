package com.Trip.Trip360.data

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

    @Query("delete from Booking where firebaseSync = 1 and webName = :currentWebName")
    suspend fun deleteAllSyncedRecordsForWebName(currentWebName: String)

    @Query("select * from Booking where firebaseSync = 0 and webName = :currentWebName")
    suspend fun getMissedRecordsForWebName(currentWebName: String): List<Booking>

}
