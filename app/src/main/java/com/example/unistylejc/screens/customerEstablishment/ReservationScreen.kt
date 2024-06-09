package com.example.unistylejc.screens.customerEstablishment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.PaymentMethod
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.screens.resources.DatePicker
import com.example.unistylejc.screens.resources.DatePickerDialog
import com.example.unistylejc.screens.resources.TimeSlots
import com.example.unistylejc.screens.resources.rememberDatePickerState
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationScreen(
    navController: NavHostController,
    workerId: String,
    establishmentId: String,
    viewModel: CustomerEstablishmentViewModel = viewModel()
) {
    val establishment by viewModel.establishment.observeAsState()
    val worker by viewModel.selectedWorker.observeAsState()
    val loggedCustomer by viewModel.loggedCustomer.observeAsState()
    val services by viewModel.selectedWorkerServices.observeAsState()
    val paymentMethods by viewModel.paymentMethods.observeAsState()

    var selectedService by remember { mutableStateOf<Service?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDateTime.now().minusHours(5).toLocalDate()) }
    var selectedTimeRange by remember { mutableStateOf<Pair<LocalTime, LocalTime>?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    var serviceMenuExpanded by remember { mutableStateOf(false) }
    var paymentMethodMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getLoggedCustomer()
        viewModel.loadEstablishment(establishmentId)
        viewModel.loadWorker(workerId)
    }

    establishment?.let {
        worker?.let { w ->
            LaunchedEffect(w.id) {
                viewModel.loadWorkerServices(w.id)
                viewModel.loadPaymentMethods()
                viewModel.loadWorkerReservations(w.id)
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reservar Servicio") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigate("establishmentDetail/${establishment?.id}") }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxWidth() // Ensure the column doesn't expand indefinitely
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Seleccionar Servicio", style = MaterialTheme.typography.bodyLarge)
                    ExposedDropdownMenuBox(
                        expanded = serviceMenuExpanded,
                        onExpandedChange = { serviceMenuExpanded = !serviceMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedService?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Servicio") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { serviceMenuExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = serviceMenuExpanded,
                            onDismissRequest = { serviceMenuExpanded = false }
                        ) {
                            services?.forEach { service ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = service.name)
                                    },
                                    onClick = {
                                        selectedService = service
                                        serviceMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Seleccionar metodo de pago",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    ExposedDropdownMenuBox(
                        expanded = paymentMethodMenuExpanded,
                        onExpandedChange = {
                            paymentMethodMenuExpanded = !paymentMethodMenuExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedPaymentMethod?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar método de pago") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentMethodMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { paymentMethodMenuExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = paymentMethodMenuExpanded,
                            onDismissRequest = { paymentMethodMenuExpanded = false }
                        ) {
                            paymentMethods?.forEach { paymentMethod ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = paymentMethod.name)
                                    },
                                    onClick = {
                                        selectedPaymentMethod = paymentMethod
                                        paymentMethodMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Seleccionar Fecha", style = MaterialTheme.typography.bodyLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Date")
                        }
                        Text(
                            text = selectedDate.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Date")
                        }
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    selectedDate?.let {
                        key(selectedDate) {
                            Text(
                                text = "Seleccionar Franja Horaria",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                TimeSlots(
                                    viewModel = viewModel,
                                    date = it,
                                    selectedTimeRange = selectedTimeRange
                                ) { range ->
                                    selectedTimeRange = range
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { navController.navigate("establishmentDetail/${establishment?.id}") }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            if (selectedDate != null && selectedTimeRange != null && loggedCustomer != null && selectedService != null && selectedPaymentMethod != null) {
                                try {
                                    val initDate = Timestamp(selectedDate!!.atTime(selectedTimeRange!!.first).toInstant(
                                        ZoneOffset.ofHours(-5)).toEpochMilli() / 1000, 0)
                                    val finishDate = Timestamp(selectedDate!!.atTime(selectedTimeRange!!.second).toInstant(
                                        ZoneOffset.ofHours(-5)).toEpochMilli() / 1000, 0)

                                    val reservation = establishment?.let {
                                        worker?.let { it1 ->
                                            Reservation(
                                                id = UUID.randomUUID().toString(),
                                                customerId = loggedCustomer!!.id,
                                                workerId = it1.id,
                                                serviceId = selectedService!!.id,
                                                establishmentId = it.id,
                                                initDate = initDate,
                                                finishDate = finishDate,
                                                paymentMethodId = selectedPaymentMethod!!.id
                                            )
                                        }
                                    }
                                    if (reservation != null) {
                                        viewModel.createReservation(reservation)
                                    };
                                    navController.navigate("establishmentDetail/${establishment?.id}")
                                } catch (e: Exception) {
                                    Log.e("ReservationScreen", "Error creating reservation: ${e.message}")
                                }
                            } else {
                                Log.e("ReservationScreen", "One of the required fields is null: selectedDate=$selectedDate, selectedTimeRange=$selectedTimeRange, loggedCustomer=$loggedCustomer, selectedService=$selectedService")
                            }
                        }) {
                            Text("Reservar")
                        }
                    }
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    title = "Selecciona la fecha",
                    onDismissRequest = { showDatePicker = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    DatePicker(
                        state = datePickerState,
                        onDismissRequest = { showDatePicker = false },
                        onDateSelected = { date ->
                            selectedDate = date
                            showDatePicker = false
                        }
                    )
                }
            }
        }
    }
}
