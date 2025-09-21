package dev.thalha.cabslip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thalha.cabslip.R
import dev.thalha.cabslip.data.database.CabSlipDatabase
import dev.thalha.cabslip.data.entity.Receipt
import dev.thalha.cabslip.data.repository.CabSlipRepository
import java.text.SimpleDateFormat
import java.util.*

// Data class for stats summary
data class StatsSummary(
    val totalReceipts: Int = 0,
    val totalKilometers: Double = 0.0,
    val totalRevenue: Double = 0.0
)

@Composable
fun HomeScreen(
    onReceiptClick: (String) -> Unit
) {
    val context = LocalContext.current
    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())

    val recentReceipts by repository.getRecentReceipts(3).collectAsState(initial = emptyList())
    var statsLoading by remember { mutableStateOf(true) }
    var statsSummary by remember { mutableStateOf(StatsSummary()) }

    // Load stats data
    LaunchedEffect(Unit) {
        try {
            val receiptsCount = repository.getTotalReceiptsCount()
            val totalKm = repository.getTotalKilometers()
            val totalRevenue = repository.getTotalRevenue()

            statsSummary = StatsSummary(
                totalReceipts = receiptsCount,
                totalKilometers = totalKm,
                totalRevenue = totalRevenue
            )
        } catch (e: Exception) {
            // Handle error silently, keep default values
        } finally {
            statsLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Summary Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Business Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (statsLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Total Receipts
                        StatCard(
                            iconRes = R.drawable.outline_receipt_24,
                            value = statsSummary.totalReceipts.toString(),
                            label = "Receipts",
                            modifier = Modifier.weight(1f)
                        )

                        // Total Kilometers
                        StatCard(
                            iconRes = R.drawable.outline_mode_of_travel_24, // Changed from outline_home_24 to proper travel icon
                            value = "${String.format(Locale.getDefault(), "%.1f", statsSummary.totalKilometers)} km",
                            label = "Distance",
                            modifier = Modifier.weight(1f)
                        )

                        // Total Revenue
                        StatCard(
                            iconRes = R.drawable.baseline_attach_money_24,
                            value = "₹${String.format(Locale.getDefault(), "%.0f", statsSummary.totalRevenue)}",
                            label = "Revenue",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Receipts Section
        Text(
            text = "Recent Receipts",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (recentReceipts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No receipts yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to create your first receipt",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentReceipts) { receipt ->
                    ReceiptCard(
                        receipt = receipt,
                        onClick = { onReceiptClick(receipt.receiptId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    iconRes: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ReceiptCard(
    receipt: Receipt,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val date = Date(receipt.tripStartDate)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = receipt.receiptId,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${receipt.boardingLocation} → ${receipt.destination}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateFormat.format(date),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%.2f", receipt.totalFee)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        painter = painterResource(
                            id = if (receipt.ownerSignaturePath != null)
                                R.drawable.outline_receipt_24 else R.drawable.baseline_warning_24
                        ),
                        contentDescription = if (receipt.ownerSignaturePath != null)
                            "Signed" else "No signature",
                        tint = if (receipt.ownerSignaturePath != null)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
