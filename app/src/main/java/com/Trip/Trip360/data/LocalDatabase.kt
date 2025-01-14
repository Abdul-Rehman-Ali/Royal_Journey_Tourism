//package com.Trip.Trip360.data
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase

//@Database(entities = [Invoice::class], version = 5, exportSchema = false) // Updated to version 5
//abstract class LocalDatabase : RoomDatabase() {
//
//    abstract fun bookingDao(): BookingDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: LocalDatabase? = null
//
//        // Migration from version 3 to 4: Adds the invoiceId column
//        private val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Booking ADD COLUMN invoiceId TEXT NOT NULL DEFAULT ''")
//            }
//        }
//
//        // Migration from version 4 to 5: Adds the totalPrice column
//        private val MIGRATION_4_5 = object : Migration(4, 5) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Booking ADD COLUMN totalPrice REAL NOT NULL DEFAULT 0.0")
//            }
//        }
//
//        fun getDatabase(context: Context): LocalDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    LocalDatabase::class.java,
//                    "local_database"
//                )
//                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5) // Add all migrations
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

//package com.Trip.Trip360.data
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase
//
//@Database(entities = [Invoice::class], version = 6, exportSchema = false) // Updated to version 6
//abstract class LocalDatabase : RoomDatabase() {
//
//    abstract fun bookingDao(): BookingDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: LocalDatabase? = null
//
//        // Migration from version 3 to 4: Adds the invoiceId column
//        private val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Booking ADD COLUMN invoiceId TEXT NOT NULL DEFAULT ''")
//            }
//        }
//
//        // Migration from version 4 to 5: Adds the totalPrice column
//        private val MIGRATION_4_5 = object : Migration(4, 5) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Booking ADD COLUMN totalPrice REAL NOT NULL DEFAULT 0.0")
//            }
//        }
//
//        // Migration from version 5 to 6: Add your new schema changes here
//        // Migration from version 5 to 6: Add the logoUrl column
//        private val MIGRATION_5_6 = object : Migration(5, 6) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Adding the new column for logoUrl
//                database.execSQL("ALTER TABLE Invoice ADD COLUMN logoUrl TEXT DEFAULT NULL")
//            }
//        }
//
//
//        fun getDatabase(context: Context): LocalDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    LocalDatabase::class.java,
//                    "local_database"
//                )
//                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6) // Add all migrations
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

package com.Trip.Trip360.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Invoice::class], version = 6, exportSchema = false) // Updated to version 6
abstract class LocalDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        // Migration from version 3 to 4: Adds the invoiceId column
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Booking ADD COLUMN invoiceId TEXT NOT NULL DEFAULT ''")
            }
        }

        // Migration from version 4 to 5: Adds the totalPrice column
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Booking ADD COLUMN totalPrice REAL NOT NULL DEFAULT 0.0")
            }
        }

        // Migration from version 5 to 6: Adds the logoUrl column to Invoice
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursor = database.query("PRAGMA table_info(Invoice)")
                var columnExists = false
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    if (columnName == "logoUrl") {
                        columnExists = true
                        break
                    }
                }
                cursor.close()

                if (!columnExists) {
                    database.execSQL("ALTER TABLE Invoice ADD COLUMN logoUrl TEXT DEFAULT NULL")
                }
            }
        }

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "local_database"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6) // Add all migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
