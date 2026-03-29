package com.example.wallettestversion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Int,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)