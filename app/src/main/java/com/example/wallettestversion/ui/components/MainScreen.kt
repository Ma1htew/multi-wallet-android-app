package com.example.wallettestversion.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallettestversion.ui.screens.accounts.AccountDetailScreen
import com.example.wallettestversion.ui.screens.accounts.AccountsScreen
import com.example.wallettestversion.ui.screens.home.HomeScreen
import com.example.wallettestversion.ui.screens.HomeViewModel
import com.example.wallettestversion.ui.screens.settings.SettingsScreen
import com.example.wallettestversion.ui.theme.MultiWalletTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: HomeViewModel = hiltViewModel()

    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    MultiWalletTheme(darkTheme = isDarkTheme) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    val items = listOf(
                        Screen("home", "Главная", Icons.Default.Home),
                        Screen("accounts", "Счета", Icons.Default.ShoppingCart),
                        Screen("settings", "Настройки", Icons.Default.Settings)
                    )

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                }

                composable("accounts") {
                    AccountsScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                        onAccountClick = { accountId ->
                            navController.navigate("account_detail/$accountId")
                        }
                    )
                }

                composable(
                    route = "account_detail/{accountId}",
                    arguments = listOf(navArgument("accountId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val accountId = backStackEntry.arguments?.getInt("accountId") ?: 0
                    AccountDetailScreen(
                        accountId = accountId,
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
)