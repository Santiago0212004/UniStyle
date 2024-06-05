package com.example.unistylejc.screens.customerEstablishment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.PaymentMethod
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.screens.resources.DatePicker
import com.example.unistylejc.screens.resources.DatePickerDialog
import com.example.unistylejc.screens.resources.TimePickerDialog
import com.example.unistylejc.screens.resources.rememberDatePickerState
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationSection(viewModel: CustomerEstablishmentViewModel, establishment: Establishment) {

    val selectedWorker by viewModel.selectedWorker.observeAsState()
    var showReservationDialog by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(16.dp))
    LazyRow(
        modifier = Modifier.wrapContentHeight()
    ) {
        if (establishment.workersRefs.isNotEmpty()) {
            items(establishment.workersRefs) { workerRef ->
                if(workerRef != ""){
                    WorkerThumbnail(viewModel, workerId = workerRef, onSelect = { viewModel.loadWorker(it.id) })
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    selectedWorker?.let { worker ->
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "¡Te atenderá ${worker.name}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showReservationDialog = true }, modifier = Modifier.align(Alignment.End)) {
                    Text(text = "Reservar")
                }
            }
        }
    } ?: run {
        Text(text = "Seleccione un trabajador", style = MaterialTheme.typography.bodyLarge)
    }


    if (showReservationDialog) {
        selectedWorker?.let {
            ReservationDialog(
                viewModel = viewModel,
                worker = it,
                establishment = establishment,
                onDismissRequest = { showReservationDialog = false },
                onConfirmRequest = { reservation ->
                    showReservationDialog = false
                    viewModel.createReservation(reservation)
                }
            )
        }
    }
}

@Composable
fun WorkerThumbnail(viewModel: CustomerEstablishmentViewModel, workerId: String, onSelect: (Worker) -> Unit) {
    val worker by produceState<Worker?>(initialValue = null) {
        value = viewModel.findWorkerById(workerId)
    }

    worker?.let {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onSelect(it) }
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = it.picture),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Gray, CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = it.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationDialog(
    viewModel: CustomerEstablishmentViewModel,
    worker: Worker,
    establishment: Establishment,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (Reservation) -> Unit
) {
    val loggedCustomer by viewModel.loggedCustomer.observeAsState()
    val services by viewModel.selectedWorkerServices.observeAsState()
    val paymentMethods by viewModel.paymentMethods.observeAsState()

    var selectedService by remember { mutableStateOf<Service?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var serviceMenuExpanded by remember { mutableStateOf(false) }
    var paymentMethodMenuExpanded by remember { mutableStateOf(false) }

    viewModel.loadWorkerServices(worker.id)
    viewModel.loadPaymentMethods()

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        content = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Reservar Servicio", style = MaterialTheme.typography.titleMedium)

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

                    Text(text = "Seleccionar metodo de pago", style = MaterialTheme.typography.bodyLarge)
                    ExposedDropdownMenuBox(
                        expanded = paymentMethodMenuExpanded,
                        onExpandedChange = { paymentMethodMenuExpanded = !paymentMethodMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedPaymentMethod?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar metodo de pago") },
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
                    Button(onClick = { showDatePicker = true }) {
                        Text(text = selectedDate?.toString() ?: "Seleccionar Fecha")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Seleccionar Hora", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = { showTimePicker = true }) {
                        Text(text = selectedTime?.toString() ?: "Seleccionar Hora")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDismissRequest() }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            if (selectedDate != null && selectedTime != null && loggedCustomer != null && selectedService != null && selectedPaymentMethod != null) {
                                try {
                                    val initDate = Timestamp(selectedDate!!.atTime(selectedTime!!).toInstant(ZoneOffset.ofHours(-5)).toEpochMilli() / 1000, 0)
                                    val finishDate = Timestamp(initDate.seconds + 1800, 0)
                                    onConfirmRequest(Reservation(
                                        id = UUID.randomUUID().toString(),
                                        customerId = loggedCustomer!!.id,
                                        workerId = worker.id,
                                        serviceId = selectedService!!.id,
                                        establishmentId = establishment.id,
                                        initDate = initDate,
                                        finishDate = finishDate,
                                        paymentMethodId = selectedPaymentMethod!!.id
                                    ))
                                    onDismissRequest()
                                } catch (e: Exception) {
                                    Log.e("ReservationDialog", "Error creating reservation: ${e.message}")
                                }
                            } else {
                                Log.e("ReservationDialog", "One of the required fields is null: selectedDate=$selectedDate, selectedTime=$selectedTime, loggedCustomer=$loggedCustomer, selectedService=$selectedService")
                            }
                        }) {
                            Text("Reservar")
                        }
                    }
                }
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            title = "Select Date",
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

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

