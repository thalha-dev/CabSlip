package dev.thalha.cabslip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import dev.thalha.cabslip.data.repository.CabSlipRepository
import dev.thalha.cabslip.data.entity.Receipt
import java.text.SimpleDateFormat
import java.util.*

// Pagination state data class
data class PaginationState(
    val items: List<Receipt> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreItems: Boolean = true,
    val currentPage: Int = 0,
    val totalCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptsScreen(
    onReceiptClick: (String) -> Unit
) {
    val context = LocalContext.current
    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())

    var searchQuery by remember { mutableStateOf("") }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndDate by remember { mutableStateOf<Long?>(null) }
    var isDateFilterActive by remember { mutableStateOf(false) }

    var paginationState by remember { mutableStateOf(PaginationState()) }
    val listState = rememberLazyListState()

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    // Constants
    val pageSize = 20

    // Load initial data and handle filter changes
    LaunchedEffect(searchQuery, isDateFilterActive, selectedStartDate, selectedEndDate) {
        paginationState = paginationState.copy(isLoading = true)
        paginationState = loadReceipts(
            repository = repository,
            searchQuery = searchQuery,
            isDateFilterActive = isDateFilterActive,
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            pageSize = pageSize,
            reset = true
        )
    }

    // Infinite scroll detection
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= paginationState.items.size - 3 &&
                    !paginationState.isLoading &&
                    paginationState.hasMoreItems) {

                    paginationState = paginationState.copy(isLoading = true)
                    paginationState = loadReceipts(
                        repository = repository,
                        searchQuery = searchQuery,
                        isDateFilterActive = isDateFilterActive,
                        selectedStartDate = selectedStartDate,
                        selectedEndDate = selectedEndDate,
                        pageSize = pageSize,
                        reset = false,
                        currentState = paginationState
                    )
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "All Receipts",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.isNotBlank()) {
                    isDateFilterActive = false
                }
            },
            label = { Text("Search receipts...") },
            leadingIcon = { Icon(painterResource(id = R.drawable.baseline_search_24), contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date range filter section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isDateFilterActive) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter by Date Range",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )

                    if (isDateFilterActive) {
                        IconButton(
                            onClick = {
                                isDateFilterActive = false
                                selectedStartDate = null
                                selectedEndDate = null
                            }
                        ) {
                            Icon(
                                painterResource(id = R.drawable.outline_delete_24),
                                contentDescription = "Clear date filter",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Single date range picker button
                OutlinedButton(
                    onClick = { showDateRangePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.outline_date_range_24),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            selectedStartDate != null && selectedEndDate != null -> {
                                "${dateFormatter.format(Date(selectedStartDate!!))} - ${dateFormatter.format(Date(selectedEndDate!!))}"
                            }
                            selectedStartDate != null -> {
                                "From ${dateFormatter.format(Date(selectedStartDate!!))} onwards"
                            }
                            else -> "Select Date Range"
                        },
                        fontSize = 14.sp
                    )
                }

                // Apply/Clear filter buttons
                if (selectedStartDate != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isDateFilterActive) {
                            Button(
                                onClick = {
                                    isDateFilterActive = true
                                    searchQuery = "" // Clear search when applying date filter
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Apply Filter")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                isDateFilterActive = false
                                selectedStartDate = null
                                selectedEndDate = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear")
                        }
                    }
                }

                // Active filter indicator
                if (isDateFilterActive && selectedStartDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            selectedEndDate != null ->
                                "Showing receipts from ${dateFormatter.format(Date(selectedStartDate!!))} to ${dateFormatter.format(Date(selectedEndDate!!))}"
                            else ->
                                "Showing receipts on ${dateFormatter.format(Date(selectedStartDate!!))}"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Results count indicator
        if (paginationState.items.isNotEmpty() || paginationState.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${paginationState.items.size} of ${paginationState.totalCount} receipts",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (paginationState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Results section
        if (paginationState.items.isEmpty() && !paginationState.isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = when {
                            isDateFilterActive -> "No receipts found in date range"
                            searchQuery.isNotBlank() -> "No matching receipts"
                            else -> "No receipts found"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            isDateFilterActive -> "Try selecting a different date range"
                            searchQuery.isNotBlank() -> "Try adjusting your search terms"
                            else -> "Create your first receipt using the + button"
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(paginationState.items) { receipt ->
                    ReceiptCard(
                        receipt = receipt,
                        onClick = { onReceiptClick(receipt.receiptId) }
                    )
                }

                // Loading indicator at the bottom
                if (paginationState.isLoading && paginationState.items.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // Date Range Picker Dialog
    if (showDateRangePicker) {
        DateRangePickerDialog(
            onDateRangeSelected = { startDate, endDate ->
                selectedStartDate = startDate
                selectedEndDate = endDate
                // Auto-apply filter when dates are selected
                if (startDate != null) {
                    isDateFilterActive = true
                    searchQuery = "" // Clear search when applying date filter
                }
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false },
            initialStartDate = selectedStartDate,
            initialEndDate = selectedEndDate
        )
    }
}

// Helper function to load receipts with pagination
private suspend fun loadReceipts(
    repository: CabSlipRepository,
    searchQuery: String,
    isDateFilterActive: Boolean,
    selectedStartDate: Long?,
    selectedEndDate: Long?,
    pageSize: Int,
    reset: Boolean,
    currentState: PaginationState = PaginationState()
): PaginationState {
    val offset = if (reset) 0 else currentState.items.size

    return try {
        val (newReceipts, totalCount) = when {
            searchQuery.isNotBlank() -> {
                val receipts = repository.searchReceiptsPaginated(searchQuery, pageSize, offset)
                val count = repository.getSearchResultsCount(searchQuery)
                receipts to count
            }
            isDateFilterActive && selectedStartDate != null -> {
                val startDate = selectedStartDate
                val endDate = if (selectedEndDate != null) {
                    selectedEndDate + (24 * 60 * 60 * 1000) - 1
                } else {
                    startDate + (24 * 60 * 60 * 1000) - 1
                }
                val receipts = repository.filterByDateRangePaginated(startDate, endDate, pageSize, offset)
                val count = repository.getDateRangeResultsCount(startDate, endDate)
                receipts to count
            }
            else -> {
                val receipts = repository.getReceiptsPaginated(pageSize, offset)
                val count = repository.getTotalReceiptsCount()
                receipts to count
            }
        }

        val updatedItems = if (reset) newReceipts else currentState.items + newReceipts
        val hasMoreItems = updatedItems.size < totalCount

        PaginationState(
            items = updatedItems,
            isLoading = false,
            hasMoreItems = hasMoreItems,
            currentPage = if (reset) 1 else currentState.currentPage + 1,
            totalCount = totalCount
        )

    } catch (_: Exception) {
        currentState.copy(isLoading = false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerDialog(
    onDateRangeSelected: (Long?, Long?) -> Unit,
    onDismiss: () -> Unit,
    initialStartDate: Long? = null,
    initialEndDate: Long? = null
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDate,
        initialSelectedEndDateMillis = initialEndDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select Date Range",
                    modifier = Modifier.padding(16.dp)
                )
            },
            headline = {
                Text(
                    text = "Choose start and end dates (end date optional)",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}
