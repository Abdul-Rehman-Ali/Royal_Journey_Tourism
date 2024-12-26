package com.Trip.Trip360.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInvoice(invoice: Invoice)

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Query("select  * from Invoice where invoiceId =:invoiceId")
    suspend fun getInvoiceById(invoiceId: Long): Invoice?

    @Query("select  * from Invoice")
    suspend fun getAll(): List<Invoice>

    @Query("delete from Invoice where firebaseSync = 1 and webName = :currentWebName")
    suspend fun deleteAllSyncedRecordsForWebName(currentWebName: String)

    @Query("select * from Invoice where firebaseSync = 0 and webName = :currentWebName")
    suspend fun getMissedRecordsForWebName(currentWebName: String): List<Invoice>

}
