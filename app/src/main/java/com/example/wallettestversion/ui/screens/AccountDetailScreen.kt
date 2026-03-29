package com.example.wallettestversion.ui.screens.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wallettestversion.data.local.Transaction
import com.example.wallettestversion.ui.screens.AddTransactionDialog
import com.example.wallettestversion.ui.screens.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    accountId: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
){
    val account by remember {
        derivedStateOf { viewModel.accounts.value.find { it.id == accountId } }
    }
    val transactions by viewModel.getTransactionsByAccount(accountId)
        .collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(account?.name ?: "Счёт") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить операцию")
            }
        }
    ) { scaffoldPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {

            // Баланс счёта
            account?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Текущий баланс", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${"%.2f".format(it.amount)} ${it.currency}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            Text(
                "Операции",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "По этому счёту пока нет операций\nНажмите + чтобы добавить",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { tx ->
                        TransactionItem(tx, dateFormatter)
                    }
                }
            }
        }


        if (showAddDialog) {
            AddTransactionDialog(
                accountId = accountId,
                onSave = { tx ->
                    viewModel.addTransaction(tx)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false }
            )
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction, dateFormatter: SimpleDateFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    tx.note.ifBlank { if (tx.amount >= 0) "Пополнение" else "Расход" },
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    dateFormatter.format(Date(tx.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${if (tx.amount >= 0) "+" else ""}${"%.2f".format(tx.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = if (tx.amount >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}