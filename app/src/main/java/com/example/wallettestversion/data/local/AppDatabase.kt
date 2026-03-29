package com.example.wallettestversion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Account::class, Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): WalletDao
}