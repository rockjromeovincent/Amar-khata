package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.database.dao.CustomerDao
import com.example.core.database.dao.SupplierDao
import com.example.core.database.dao.TransactionDao
import com.example.core.database.dao.UserDao
import com.example.core.database.entities.CustomerEntity
import com.example.core.database.entities.SupplierEntity
import com.example.core.database.entities.TransactionEntity
import com.example.core.database.entities.UserEntity

@Database(
    entities = [
        TransactionEntity::class,
        CustomerEntity::class,
        SupplierEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amar_khata_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
