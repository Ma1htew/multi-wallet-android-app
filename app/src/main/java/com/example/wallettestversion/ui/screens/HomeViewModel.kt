package com.example.wallettestversion.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallettestversion.data.local.Account
import com.example.wallettestversion.data.local.Transaction
import com.example.wallettestversion.domain.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    val allTransactions: StateFlow<List<Transaction>> =
        repository.getAllTransactions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _baseCurrency = MutableStateFlow("RUB")
    val baseCurrency: StateFlow<String> = _baseCurrency.asStateFlow()

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()


    val totalBalance: StateFlow<Double> =
        combine(accounts, rates, baseCurrency) { accs, r, base ->
            if (r.isEmpty()) {
                accs.sumOf { it.amount }
            } else {
                accs.sumOf { account ->
                    val rate = r[account.currency] ?: 1.0
                    if (account.currency == base) {
                        account.amount
                    } else if (rate > 0.0) {
                        account.amount / rate
                    } else {
                        account.amount
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            repository.getAccounts().collect { _accounts.value = it }
        }
        loadRates()
    }

    fun addAccount(account: Account) = viewModelScope.launch {
        repository.addAccount(account)
    }

    fun addTransaction(tx: Transaction) = viewModelScope.launch {
        repository.addTransaction(tx)
        val current = _accounts.value.find { it.id == tx.accountId } ?: return@launch
        val updated = current.copy(amount = current.amount + tx.amount)
        repository.updateAccount(updated)
    }

    fun clearAllData() = viewModelScope.launch {
        repository.clearAllData()
        _accounts.value = emptyList()
    }

    fun changeCurrency(new: String) {
        if (new == _baseCurrency.value) return

        _baseCurrency.value = new
        viewModelScope.launch {
            val newRates = repository.getRates(new)
            _rates.value = newRates
        }
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun deleteAccount(account: Account) = viewModelScope.launch {
        repository.deleteTransactionsByAccount(account.id)
        repository.deleteAccount(account)
        _accounts.value = repository.getAccounts().first()
    }

    fun getTransactionsByAccount(accountId: Int): Flow<List<Transaction>> =
        repository.getTransactionsByAccount(accountId)

    private fun loadRates() = viewModelScope.launch {
        val newRates = repository.getRates(_baseCurrency.value)
        _rates.value = newRates
        println("✅ Rates updated for ${_baseCurrency.value}, size = ${newRates.size}")
    }
}