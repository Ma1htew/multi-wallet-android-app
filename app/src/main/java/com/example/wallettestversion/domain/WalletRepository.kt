package com.example.wallettestversion.domain

import com.example.wallettestversion.data.local.Account
import com.example.wallettestversion.data.local.Transaction
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getAccounts(): Flow<List<Account>>
    suspend fun addAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    suspend fun deleteTransactionsByAccount(accountId: Int)


    suspend fun clearAllData()

    suspend fun getRates(base: String): Map<String, Double>

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByAccount(accountId: Int): Flow<List<Transaction>>
    suspend fun addTransaction(tx: Transaction)
}