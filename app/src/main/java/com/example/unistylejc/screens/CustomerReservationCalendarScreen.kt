package com.example.unistylejc.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.ReservationEntity
import com.example.unistylejc.viewmodel.CustomerReservationsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CustomerReservationCalendarScreen(navController: NavHostController, viewModel: CustomerReservationsViewModel = viewModel()) {
    val isAuthenticated by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    val pastReservations by viewModel.pastReservations.observeAsState()
    val upcomingReservations by viewModel.futureReservations.observeAsState()
    var initialUpcomingShown by remember { mutableStateOf(0) }
    var initialPastShown by remember { mutableStateOf(0) }
    var upcomingReservationsShown by remember { mutableStateOf(0) }
    var pastReservationsShown by remember { mutableStateOf(0) }

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadCustomerReservations()
        }
    }

    LaunchedEffect(upcomingReservations) {
        upcomingReservations?.let {
            upcomingReservationsShown = it.size.coerceAtMost(2)
            initialUpcomingShown = upcomingReservationsShown
        }
    }
    LaunchedEffect(pastReservations) {
        pastReservations?.let {
            pastReservationsShown = it.size.coerceAtMost(2)
            initialPastShown = pastReservationsShown
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Reservas próximas", fontSize = 36.sp, fontWeight = FontWeight.Bold )
        }
        upcomingReservations?.let { reservations ->
            if (reservations.isNotEmpty()) {
                items(reservations.take(upcomingReservationsShown)) { reservation ->
                    UpcomingReservationCardCustomer(reservation, viewModel, navController)
                }
            } else {
                item {
                    NoReservationsMessageCustomer()
                }
            }
        }

        item {
            upcomingReservations?.let { upcomingReservations ->
                if (upcomingReservationsShown < upcomingReservations.size) {
                    Button(
                        onClick = { upcomingReservationsShown += 1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5D16A6)
                        ),
                    ) {
                        Text("Ver más")
                    }
                } else {
                    if (upcomingReservations.isNotEmpty()) {
                        Button(
                            onClick = { upcomingReservationsShown = initialUpcomingShown },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5D16A6)
                            ),
                        ) {
                            Text("Ver menos")
                        }
                    }
                }
            }
        }

        item {
            Text("Reservas anteriores", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        }
        pastReservations?.let { pastReservations ->
            items(pastReservations.take(pastReservationsShown)) { reservation ->
                ReservationCardCustomer(reservation)
            }
            item {
                if (pastReservationsShown < pastReservations.size) {
                    Button(
                        onClick = { pastReservationsShown += 1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5D16A6)
                        ),
                    ) {
                        Text("Ver más...")
                    }
                } else {
                    if (pastReservations.isNotEmpty()) {
                        Button(
                            onClick = { pastReservationsShown = initialPastShown },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5D16A6)
                            ),
                        ) {
                            Text("Ver menos...")

                        }
                    }else{
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text( "Parece que no aún no tienes reservas previas...", fontSize = 28.sp, textAlign = TextAlign.Center,fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCardCustomer(reservation: ReservationEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF2E8FC), shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(reservation.establishment?.picture),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {


                reservation.initDate?.toDate()?.let {

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    val date = reservation.initDate?.toDate()?.let { d -> sdf.format(d) } ?: "Unknown date"
                    val time = reservation.initDate?.toDate()?.let { t -> sdfTime.format(t) } ?: "Unknown time"

                    reservation.establishment?.let { e -> Text(e.name, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Servicio: ${reservation.service?.name}")
                    Text("Trabajador: ${reservation.worker?.name}")
                    Text("Precio: $${reservation.service?.price}")
                    Text("Día: $date")
                    Text("Hora: $time")
                    reservation.establishment?.address?.let { a -> Text(a, fontSize = 12.sp, color = Color.Gray) }
                }

            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.Start) {
                    Icon(
                        painter = painterResource(
                            id = when (reservation.paymentMethod?.name) {
                                "Efectivo" -> R.drawable.ic_cash
                                "Daviplata" -> R.drawable.ic_credit_card
                                else -> R.drawable.ic_credit_card
                            }
                        ),
                        contentDescription = reservation.paymentMethod?.name ?: "Unknown",
                        modifier = Modifier.size(12.dp)
                    )
                    reservation.paymentMethod?.name?.let {
                        Text(it, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingReservationCardCustomer(reservation: ReservationEntity, viewModel: CustomerReservationsViewModel, navController: NavHostController) {
    var showDialogDR by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF2E8FC), shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(reservation.establishment?.picture),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {


                reservation.initDate?.toDate()?.let {

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    val date = reservation.initDate?.toDate()?.let { d -> sdf.format(d) } ?: "Unknown date"
                    val time = reservation.initDate?.toDate()?.let { t -> sdfTime.format(t) } ?: "Unknown time"

                    reservation.establishment?.let { e -> Text(e.name, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Servicio: ${reservation.service?.name}")
                    Text("Trabajador: ${reservation.worker?.name}")
                    Text("Precio: $${reservation.service?.price}")
                    Text("Día: $date")
                    Text("Hora: $time")
                    reservation.establishment?.address?.let { a -> Text(a, fontSize = 12.sp, color = Color.Gray) }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.Start) {
                    Icon(
                        painter = painterResource(
                            id = when (reservation.paymentMethod?.name) {
                                "Efectivo" -> R.drawable.ic_cash
                                "Daviplata" -> R.drawable.ic_credit_card
                                else -> R.drawable.ic_credit_card
                            }
                        ),
                        contentDescription = reservation.paymentMethod?.name ?: "Unknown",
                        modifier = Modifier.size(12.dp)
                    )
                    reservation.paymentMethod?.name?.let {
                        Text(it, fontSize = 11.sp)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(modifier = Modifier.clickable {
                showDialogDR = true
            }, imageVector = Icons.Default.Delete, contentDescription = "Delete",
                tint = Color.Red)
        }

        if (showDialogDR) {
            DeleteReservationConfirmationDialog(
                onConfirm = {
                    viewModel.deleteReservation(reservation.id, reservation.client?.id, reservation.worker?.id, reservation.establishment?.id)
                    navController.navigate("customer/agenda")
                    showDialogDR = false
                },
                onDismiss = {
                    showDialogDR = false
                }
            )
        }
    }
}


@Composable
fun NoReservationsMessageCustomer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(Color(0xFF7B40B7)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.ic_worker_error),
                contentDescription = "Error",
                modifier = Modifier.size(230.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text( "Parece que no aún no tienes reservas...", fontSize = 28.sp, textAlign = TextAlign.Center,fontWeight = FontWeight.Bold )
        }

    }
}

@Composable
fun DeleteReservationConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D16A6))
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2E8FC)),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color(0xFF5D16A6),
                    shape = RoundedCornerShape(100.dp)
                )
            ) {
                Text("Cancelar", color = Color(0xFF5D16A6))
            }
        },
        title = {
            Text(
                "¿Seguro desea cancelar la reserva?",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier
            .width(349.dp)
            .height(224.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

