package com.Trip.Trip360.data

import androidx.lifecycle.LiveData
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

    @Query("select  * from Invoice where id =:invoiceId")
    suspend fun getInvoiceById(invoiceId: Long): Invoice?

    @Query("select  * from Invoice")
    fun getAll(): LiveData<List<Invoice>>

    @Query("SELECT * FROM Invoice WHERE LOWER(name) LIKE :query OR LOWER(packageName) LIKE :query")
    fun searchByNameOrPackage(query: String): List<Invoice>

    @Query("SELECT * FROM Invoice WHERE currentDate BETWEEN :startDate AND :endDate")
    suspend fun getInvoicesByDateRange(startDate: String, endDate: String): List<Invoice>

    @Query("delete from Invoice where firebaseSync = 1 and webName = :currentWebName")
    suspend fun deleteAllSyncedRecordsForWebName(currentWebName: String)

    @Query("select * from Invoice where firebaseSync = 0 and webName = :currentWebName")
    suspend fun getMissedRecordsForWebName(currentWebName: String): List<Invoice>

}
