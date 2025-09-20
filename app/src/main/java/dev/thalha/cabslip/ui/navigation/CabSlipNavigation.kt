package dev.thalha.cabslip.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.thalha.cabslip.data.database.CabSlipDatabase
import dev.thalha.cabslip.data.repository.CabSlipRepository
import dev.thalha.cabslip.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Receipts : Screen("receipts", "Receipts", Icons.Default.List)
    object CabInfo : Screen("cab_info", "Cab Info", Icons.Default.Info)
    object CreateReceipt : Screen("create_receipt", "Create Receipt", Icons.Default.Add)
    object EditReceipt : Screen("edit_receipt/{receiptId}", "Edit Receipt", Icons.Default.Edit)
    object FirstTimeSetup : Screen("first_time_setup", "Setup", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CabSlipNavigation() {
    val context = LocalContext.current
    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())

    var isSetupComplete by remember { mutableStateOf<Boolean?>(null) }

    // Check if cab info exists (setup is complete)
    LaunchedEffect(Unit) {
        val cabInfo = repository.getCabInfoSync()
        isSetupComplete = cabInfo != null
    }

    when (isSetupComplete) {
        null -> {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        false -> {
            // Show setup screen without navigation
            FirstTimeSetupScreen(
                onSetupComplete = {
                    isSetupComplete = true
                }
            )
        }
        true -> {
            // Show main app with navigation
            MainAppNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Home tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Receipts tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Receipts") },
                    label = { Text("Receipts") },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.Receipts.route } == true,
                    onClick = {
                        navController.navigate(Screen.Receipts.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Cab Info tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Cab Info") },
                    label = { Text("Cab Info") },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.CabInfo.route } == true,
                    onClick = {
                        navController.navigate(Screen.CabInfo.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.CreateReceipt.route)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Receipt")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onReceiptClick = { receiptId ->
                        navController.navigate("edit_receipt/$receiptId")
                    }
                )
            }
            composable(Screen.Receipts.route) {
                ReceiptsScreen(
                    onReceiptClick = { receiptId ->
                        navController.navigate("edit_receipt/$receiptId")
                    }
                )
            }
            composable(Screen.CabInfo.route) {
                CabInfoScreen()
            }
            composable(Screen.CreateReceipt.route) {
                CreateReceiptScreen(
                    onReceiptSaved = {
                        navController.popBackStack()
                    }
                )
            }
            composable("edit_receipt/{receiptId}") { backStackEntry ->
                val receiptId = backStackEntry.arguments?.getString("receiptId") ?: ""
                EditReceiptScreen(
                    receiptId = receiptId,
                    onReceiptUpdated = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
