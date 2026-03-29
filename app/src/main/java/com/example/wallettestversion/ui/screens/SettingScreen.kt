package com.example.wallettestversion.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallettestversion.ui.screens.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Настройки") })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Основные",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )


            SettingCard(
                icon = Icons.Default.DarkMode,
                title = "Тёмная тема",
                subtitle = if (isDarkTheme) "Включена" else "Выключена",
                isDarkTheme = isDarkTheme,
                isDanger = false
            ) {
                viewModel.toggleDarkTheme()
            }


            SettingCard(
                icon = Icons.Default.CurrencyExchange,
                title = "Основная валюта",
                subtitle = baseCurrency,
                isDarkTheme = isDarkTheme,
                isDanger = false
            ) {
                val cycle = listOf("RUB", "USD", "EUR", "CNY", "GBP", "TRY")
                val nextIndex = (cycle.indexOf(baseCurrency) + 1) % cycle.size
                viewModel.changeCurrency(cycle[nextIndex])
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Опасная зона",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )


            SettingCard(
                icon = Icons.Default.DeleteForever,
                title = "Очистить все данные",
                subtitle = "Удалить все счета и операции",
                isDarkTheme = isDarkTheme,
                isDanger = true
            ) {
                showClearDialog = true
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Multi Wallet v1.0 - то , что не видит налоговая\uD83D\uDE0A",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить все данные?") },
            text = { Text("Это действие удалит ВСЕ счета и операции.\n\nДействие необратимо.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    }
                ) {
                    Text("Очистить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}


@Composable
fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isDarkTheme: Boolean,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isDanger -> if (isDarkTheme) Color(0xFF3F1A1A) else Color(0xFFFFEBEE)
        else -> if (isDarkTheme) Color(0xFF1E3A8A) else Color(0xFFE3F2FD)
    }

    val iconTint = when {
        isDanger -> Color(0xFFD32F2F)
        isDarkTheme -> Color.White
        else -> Color(0xFF2196F3)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}