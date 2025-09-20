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
            NavigationBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val currentRoute = currentDestination?.route

                // Home tab
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home") },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                // Create Receipt button - Simple navigation, no complex state management
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Receipt",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Create") },
                    selected = currentRoute == Screen.CreateReceipt.route,
                    onClick = {
                        navController.navigate(Screen.CreateReceipt.route) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                // Receipts tab
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Receipts",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Receipts") },
                    selected = currentRoute == Screen.Receipts.route,
                    onClick = {
                        navController.navigate(Screen.Receipts.route) {
                            popUpTo(Screen.Receipts.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                // Cab Info tab
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Cab Info",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Cab Info") },
                    selected = currentRoute == Screen.CabInfo.route,
                    onClick = {
                        navController.navigate(Screen.CabInfo.route) {
                            popUpTo(Screen.CabInfo.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        // Navigate back to home after saving with clean navigation
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
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
