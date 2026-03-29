package com.example.wallettestversion.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wallettestversion.data.local.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    accountId: Int,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(false) } // false = пополнение, true = расход

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая операция") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Тип операции
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isExpense,
                        onClick = { isExpense = false },
                        label = { Text("Пополнение") }
                    )
                    FilterChip(
                        selected = isExpense,
                        onClick = { isExpense = true },
                        label = { Text("Расход") }
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.toDoubleOrNull() != null || it.isEmpty()) amount = it },
                    label = { Text("Сумма") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка (необязательно)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val sum = amount.toDoubleOrNull() ?: 0.0
                if (sum > 0) {
                    val finalAmount = if (isExpense) -sum else sum
                    onSave(
                        Transaction(
                            accountId = accountId,
                            amount = finalAmount,
                            note = note.trim()
                        )
                    )
                    onDismiss()
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}