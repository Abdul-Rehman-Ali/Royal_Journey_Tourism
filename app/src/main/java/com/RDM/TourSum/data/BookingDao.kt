package com.RDM.TourSum.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookingDao {

    // insert -> inserts new data
    // update -> updates existing data, if not exists, does nothing
    // upsert -> if (new record): inserts, else: updates

    @Upsert
    suspend fun upsertRecord(invoiceRecord: Booking)

    @Delete
    suspend fun deleteRecord(invoiceRecord: Booking)

    @Query("delete from Booking where firebaseSync = 1")
    suspend fun deleteAllSyncedRecords()

    @Query("select * from Booking where firebaseSync = 0")
    suspend fun getMissedRecords() : List<Booking>
}