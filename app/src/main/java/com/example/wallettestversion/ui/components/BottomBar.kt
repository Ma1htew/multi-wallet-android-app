package com.example.wallettestversion.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        Triple("home", "Главная", Icons.Default.Home),
        Triple("accounts", "Счета", Icons.Default.AccountBox),
        Triple("settings", "Настройки", Icons.Default.Settings)
    )

    NavigationBar {
        val backStack by navController.currentBackStackEntryAsState()

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, null) },
                label = { Text(label) },
                selected = backStack?.destination?.route == route,
                onClick = { navController.navigate(route) }
            )
        }
    }
}