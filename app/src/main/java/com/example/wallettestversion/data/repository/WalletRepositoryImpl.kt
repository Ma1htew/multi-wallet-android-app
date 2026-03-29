package com.example.wallettestversion.data.repository

import com.example.wallettestversion.data.local.Account
import com.example.wallettestversion.data.local.Transaction
import com.example.wallettestversion.data.local.WalletDao
import com.example.wallettestversion.data.remote.CurrencyApi
import com.example.wallettestversion.domain.WalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val dao: WalletDao,
    private val api: CurrencyApi,
    private val accessKey: String
) : WalletRepository {


    override fun getAccounts(): Flow<List<Account>> = dao.getAccounts()

    override suspend fun addAccount(account: Account) =
        dao.insertAccount(account)

    override suspend fun updateAccount(account: Account) =
        dao.updateAccount(account)

    override suspend fun deleteAccount(account: Account) =
        dao.deleteAccount(account)


    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions()

    override fun getTransactionsByAccount(accountId: Int): Flow<List<Transaction>> =
        dao.getTransactionsByAccount(accountId)

    override suspend fun addTransaction(tx: Transaction) =
        dao.insertTransaction(tx)

    override suspend fun deleteTransactionsByAccount(accountId: Int) =
        dao.deleteTransactionsByAccount(accountId)

    override suspend fun clearAllData() {
        dao.clearAllTransactions()
        dao.clearAllAccounts()
        println("База данных полностью очищена")
    }


    override suspend fun getRates(base: String): Map<String, Double> {
        return try {
            val response = api.getLive(accessKey)

            if (!response.success || response.quotes.isEmpty()) {
                println("❌ API error")
                return mapOf(base to 1.0)
            }

            val quotes = response.quotes
            val ratesFromBase = mutableMapOf<String, Double>()


            ratesFromBase[base] = 1.0

            if (base == "USD") {

                quotes.forEach { (pair, rate) ->
                    if (pair.startsWith("USD") && pair.length == 6) {
                        val target = pair.substring(3)
                        if (target.isNotBlank()) {
                            ratesFromBase[target] = rate
                        }
                    }
                }
            } else {

                val usdToBaseKey = "USD$base"
                val usdToBaseRate = quotes[usdToBaseKey] ?: 1.0

                if (usdToBaseRate <= 0.0) {
                    println("⚠️ Invalid USD->$base rate")
                    return mapOf(base to 1.0)
                }


                quotes.forEach { (pair, rate) ->
                    if (pair.startsWith("USD") && pair.length == 6) {
                        val target = pair.substring(3)
                        if (target.isNotBlank()) {
                            val rateToUsd = rate
                            val rateToBase = rateToUsd / usdToBaseRate
                            ratesFromBase[target] = rateToBase
                        }
                    }
                }
            }


            if (!ratesFromBase.containsKey("USD")) {
                val usdToBaseRate = quotes["USD$base"] ?: 1.0
                if (usdToBaseRate > 0.0) {
                    ratesFromBase["USD"] = 1.0 / usdToBaseRate
                } else {
                    ratesFromBase["USD"] = 1.0
                }
            }

            println("✅ Rates loaded for $base → ${ratesFromBase.size} currencies")
            println("   USD rate in $base = ${ratesFromBase["USD"]}")
            println("   RUB rate in $base = ${ratesFromBase["RUB"]}")

            ratesFromBase
        } catch (e: Exception) {
            println("❌ Exception in getRates($base): ${e.message}")
            mapOf(base to 1.0)
        }
    }
}