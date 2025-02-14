package com.Trip.Trip360.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Invoice::class], version = 7, exportSchema = false) // ✅ Updated to version 7
abstract class LocalDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        // ✅ Migration from version 5 to 6: Adds the "color" column to Invoice table
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Invoice ADD COLUMN color TEXT NOT NULL DEFAULT '#FFFFFF'")
            }
        }

        // ✅ Migration from version 6 to 7: Adds the NEW_COLUMN to YOUR_TABLE
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE YourTable ADD COLUMN newColumnName TEXT NOT NULL DEFAULT ''") // Change table & column names accordingly
            }
        }

        // ✅ Migration from version 6 to 7: Adds the NEW_COLUMN to YOUR_TABLE
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE YourTable ADD COLUMN newColumnName TEXT NOT NULL DEFAULT ''") // Change table & column names accordingly
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE YourTable ADD COLUMN newColumnName TEXT NOT NULL DEFAULT ''") // Change table & column names accordingly
            }
        }

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "local_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9) // ✅ Added migration from v6 to v7
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
