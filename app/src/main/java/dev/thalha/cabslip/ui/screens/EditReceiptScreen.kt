package dev.thalha.cabslip.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thalha.cabslip.data.database.CabSlipDatabase
import dev.thalha.cabslip.data.entity.Receipt
import dev.thalha.cabslip.data.repository.CabSlipRepository
import dev.thalha.cabslip.ui.components.SignatureCapture
import dev.thalha.cabslip.ui.components.saveSignatureFromPath
import dev.thalha.cabslip.utils.PdfGenerator
import dev.thalha.cabslip.utils.ShareUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScreen(
    receiptId: String,
    onReceiptUpdated: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())

    var receipt by remember { mutableStateOf<Receipt?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Form state
    var boardingLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var tripStartDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var tripEndDate by remember { mutableStateOf<Long?>(null) }
    var pricePerKm by remember { mutableStateOf("") }
    var waitingChargePerHr by remember { mutableStateOf("") }
    var waitingHrs by remember { mutableStateOf("") }
    var totalKm by remember { mutableStateOf("") }
    var tollParking by remember { mutableStateOf("") }
    var bata by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var driverMobile by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var ownerSignaturePath by remember { mutableStateOf<String?>(null) }

    // Signature state
    var signaturePath by remember { mutableStateOf<Path?>(null) }
    var hasNewSignature by remember { mutableStateOf(false) }
    var existingSignaturePath by remember { mutableStateOf<String?>(null) }

    // Date formatters
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Helper function to combine date and time
    fun combineDateTime(dateMillis: Long, timeMillis: Long): Long {
        val calendar = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val timeCalendar = Calendar.getInstance().apply { timeInMillis = timeMillis }

        calendar.set(
            dateCalendar.get(Calendar.YEAR),
            dateCalendar.get(Calendar.MONTH),
            dateCalendar.get(Calendar.DAY_OF_MONTH),
            timeCalendar.get(Calendar.HOUR_OF_DAY),
            timeCalendar.get(Calendar.MINUTE)
        )
        return calendar.timeInMillis
    }

    // Load receipt data
    LaunchedEffect(receiptId) {
        try {
            val loadedReceipt = repository.getReceiptById(receiptId)
            if (loadedReceipt != null) {
                receipt = loadedReceipt

                // Populate form fields
                boardingLocation = loadedReceipt.boardingLocation
                destination = loadedReceipt.destination
                tripStartDate = loadedReceipt.tripStartDate
                tripEndDate = loadedReceipt.tripEndDate
                pricePerKm = loadedReceipt.pricePerKm.toString()
                waitingChargePerHr = loadedReceipt.waitingChargePerHr.toString()
                waitingHrs = loadedReceipt.waitingHrs.toString()
                totalKm = loadedReceipt.totalKm.toString()
                tollParking = loadedReceipt.tollParking.toString()
                bata = loadedReceipt.bata.toString()
                driverName = loadedReceipt.driverName
                driverMobile = loadedReceipt.driverMobile
                vehicleNumber = loadedReceipt.vehicleNumber
                ownerSignaturePath = loadedReceipt.ownerSignaturePath

                // Initialize signature state properly
                existingSignaturePath = loadedReceipt.ownerSignaturePath
                hasNewSignature = false
                signaturePath = null
            } else {
                errorMessage = "Receipt not found"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load receipt"
        } finally {
            isLoading = false
        }
    }

    // Calculated totals
    val calculatedTotals = remember(pricePerKm, totalKm, waitingChargePerHr, waitingHrs, tollParking, bata) {
        try {
            val priceKm = pricePerKm.toDoubleOrNull() ?: 0.0
            val km = totalKm.toDoubleOrNull() ?: 0.0
            val waitingRate = waitingChargePerHr.toDoubleOrNull() ?: 0.0
            val hours = waitingHrs.toDoubleOrNull() ?: 0.0
            val toll = tollParking.toDoubleOrNull() ?: 0.0
            val bataAmount = bata.toDoubleOrNull() ?: 0.0

            repository.calculateReceiptTotals(priceKm, km, waitingRate, hours, toll, bataAmount)
        } catch (e: Exception) {
            Triple(0.0, 0.0, 0.0)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (receipt == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Receipt not found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Edit Receipt",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Receipt ID: ${receipt?.receiptId}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Share Button - NEW PHASE 2 FEATURE
            IconButton(
                onClick = {
                    scope.launch {
                        try {
                            val cabInfo = repository.getCabInfoSync()
                            if (cabInfo != null && receipt != null) {
                                val pdfPath = PdfGenerator.generateReceiptPdf(context, receipt!!, cabInfo)
                                if (pdfPath != null) {
                                    ShareUtils.sharePdf(context, pdfPath, receipt!!.receiptId)
                                }
                            }
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share PDF")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trip Details Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Trip Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = boardingLocation,
                    onValueChange = { boardingLocation = it },
                    label = { Text("Boarding Location *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    label = { Text("Vehicle Number *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Trip Start Date and Time
                Text(
                    text = "Trip Start Date & Time *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dateFormatter.format(Date(tripStartDate)),
                    onValueChange = { },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = timeFormatter.format(Date(tripStartDate)),
                    onValueChange = { },
                    label = { Text("Start Time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Select Time")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Trip End Date and Time (Optional)
                Text(
                    text = "Trip End Date & Time (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tripEndDate?.let { dateFormatter.format(Date(it)) } ?: "",
                    onValueChange = { },
                    label = { Text("End Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Row {
                            if (tripEndDate != null) {
                                IconButton(onClick = { tripEndDate = null }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Date")
                                }
                            }
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tripEndDate?.let { timeFormatter.format(Date(it)) } ?: "",
                    onValueChange = { },
                    label = { Text("End Time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { showEndTimePicker = true },
                            enabled = tripEndDate != null
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Select Time")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Driver Details Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Driver Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = driverName,
                    onValueChange = { driverName = it },
                    label = { Text("Driver Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = driverMobile,
                    onValueChange = { driverMobile = it },
                    label = { Text("Driver Mobile") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fare Details Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fare Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = totalKm,
                        onValueChange = { totalKm = it },
                        label = { Text("Total KM *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = pricePerKm,
                        onValueChange = { pricePerKm = it },
                        label = { Text("Price/KM *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = waitingHrs,
                        onValueChange = { waitingHrs = it },
                        label = { Text("Waiting Hours") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = waitingChargePerHr,
                        onValueChange = { waitingChargePerHr = it },
                        label = { Text("Waiting Rate/Hr") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tollParking,
                        onValueChange = { tollParking = it },
                        label = { Text("Toll & Parking") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = bata,
                        onValueChange = { bata = it },
                        label = { Text("Bata") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Signature Capture Section - NEW PHASE 2 FEATURE
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SignatureCapture(
                    existingSignaturePath = ownerSignaturePath,
                    onSignatureChanged = { path, hasSignature, existingPath ->
                        signaturePath = path
                        hasNewSignature = hasSignature
                        // Don't override existingSignaturePath if we get null (new signature drawn)
                        if (existingPath != null) {
                            existingSignaturePath = existingPath
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fare Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fare Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Base Fare:")
                    Text("₹${String.format("%.2f", calculatedTotals.first)}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Waiting Fee:")
                    Text("₹${String.format("%.2f", calculatedTotals.second)}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Toll & Parking:")
                    Text("₹${String.format("%.2f", tollParking.toDoubleOrNull() ?: 0.0)}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bata:")
                    Text("₹${String.format("%.2f", bata.toDoubleOrNull() ?: 0.0)}")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Fee:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "₹${String.format("%.2f", calculatedTotals.third)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons - Updated to include Share button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    Log.d("EditReceiptScreen", "UPDATE BUTTON CLICKED!")

                    // Validate required fields
                    Log.d("EditReceiptScreen", "Validating fields:")
                    Log.d("EditReceiptScreen", "boardingLocation: '$boardingLocation'")
                    Log.d("EditReceiptScreen", "destination: '$destination'")
                    Log.d("EditReceiptScreen", "vehicleNumber: '$vehicleNumber'")
                    Log.d("EditReceiptScreen", "pricePerKm: '$pricePerKm'")
                    Log.d("EditReceiptScreen", "totalKm: '$totalKm'")

                    if (boardingLocation.isBlank() || destination.isBlank() ||
                        vehicleNumber.isBlank() || pricePerKm.isBlank() || totalKm.isBlank()) {
                        Log.d("EditReceiptScreen", "Validation FAILED - missing required fields")
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }

                    // Validate numeric fields
                    val priceKm = pricePerKm.toDoubleOrNull()
                    val km = totalKm.toDoubleOrNull()
                    Log.d("EditReceiptScreen", "Numeric validation: priceKm=$priceKm, km=$km")

                    if (priceKm == null || km == null || priceKm < 0 || km < 0) {
                        Log.d("EditReceiptScreen", "Validation FAILED - invalid numbers")
                        errorMessage = "Please enter valid positive numbers for price and distance"
                        return@Button
                    }

                    Log.d("EditReceiptScreen", "Validation PASSED - proceeding with update")
                    errorMessage = ""
                    isSaving = true

                    scope.launch {
                        try {
                            // Handle signature saving logic
                            Log.d("EditReceiptScreen", "=== SIGNATURE DEBUG ===")
                            Log.d("EditReceiptScreen", "hasNewSignature: $hasNewSignature")
                            Log.d("EditReceiptScreen", "signaturePath isEmpty: ${signaturePath?.isEmpty}")
                            Log.d("EditReceiptScreen", "existingSignaturePath: $existingSignaturePath")

                            val finalSignaturePath = if (hasNewSignature && signaturePath != null) {
                                Log.d("EditReceiptScreen", "Saving new signature...")
                                val newPath = saveSignatureFromPath(context, signaturePath, density)
                                Log.d("EditReceiptScreen", "New signature saved to: $newPath")
                                newPath
                            } else if (hasNewSignature && signaturePath == null) {
                                Log.d("EditReceiptScreen", "hasNewSignature=true but no valid path - this is a bug in SignatureCapture, keeping existing")
                                existingSignaturePath
                            } else {
                                Log.d("EditReceiptScreen", "Keeping existing signature: $existingSignaturePath")
                                existingSignaturePath
                            }

                            val currentTime = System.currentTimeMillis()

                            val updatedReceipt = receipt!!.copy(
                                boardingLocation = boardingLocation.trim(),
                                destination = destination.trim(),
                                tripStartDate = tripStartDate,
                                tripEndDate = tripEndDate,
                                pricePerKm = priceKm,
                                waitingChargePerHr = waitingChargePerHr.toDoubleOrNull() ?: 0.0,
                                waitingHrs = waitingHrs.toDoubleOrNull() ?: 0.0,
                                totalKm = km,
                                tollParking = tollParking.toDoubleOrNull() ?: 0.0,
                                bata = bata.toDoubleOrNull() ?: 0.0,
                                driverName = driverName.trim(),
                                driverMobile = driverMobile.trim(),
                                vehicleNumber = vehicleNumber.trim(),
                                ownerSignaturePath = finalSignaturePath,
                                baseFare = calculatedTotals.first,
                                waitingFee = calculatedTotals.second,
                                totalFee = calculatedTotals.third,
                                updatedAt = currentTime
                            )

                            repository.updateReceipt(updatedReceipt)
                            errorMessage = ""
                            onReceiptUpdated()
                        } catch (e: Exception) {
                            errorMessage = "Failed to update receipt. Please try again."
                        } finally {
                            isSaving = false
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Update Receipt")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Dialogs
        if (showStartDatePicker) {
            EditDatePickerDialog(
                onDateSelected = { selectedDate ->
                    tripStartDate = combineDateTime(selectedDate, tripStartDate)
                    showStartDatePicker = false
                },
                onDismiss = { showStartDatePicker = false }
            )
        }

        if (showStartTimePicker) {
            EditTimePickerDialog(
                onTimeSelected = { selectedTime ->
                    tripStartDate = combineDateTime(tripStartDate, selectedTime)
                    showStartTimePicker = false
                },
                onDismiss = { showStartTimePicker = false }
            )
        }

        if (showEndDatePicker) {
            EditDatePickerDialog(
                onDateSelected = { selectedDate ->
                    val currentEndTime = tripEndDate ?: System.currentTimeMillis()
                    tripEndDate = combineDateTime(selectedDate, currentEndTime)
                    showEndDatePicker = false
                },
                onDismiss = { showEndDatePicker = false }
            )
        }

        if (showEndTimePicker) {
            EditTimePickerDialog(
                onTimeSelected = { selectedTime ->
                    val currentEndDate = tripEndDate ?: System.currentTimeMillis()
                    tripEndDate = combineDateTime(currentEndDate, selectedTime)
                    showEndTimePicker = false
                },
                onDismiss = { showEndTimePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate ->
                        onDateSelected(selectedDate)
                    }
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
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTimePickerDialog(
    onTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState()

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            calendar.set(Calendar.MINUTE, timePickerState.minute)
                            onTimeSelected(calendar.timeInMillis)
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
