package com.example.unistylejc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.ReservationEntity
import com.example.unistylejc.viewmodel.WorkerReservationsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkerReservationsScreen(navController: NavHostController, viewModel: WorkerReservationsViewModel = viewModel()) {
    val isAuthenticated by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    val pastReservations by viewModel.pastReservations.observeAsState()
    val upcomingReservations by viewModel.futureReservations.observeAsState()
    var initialUpcomingShown by remember { mutableStateOf(0) }
    var initialPastShown by remember { mutableStateOf(0) }
    var upcomingReservationsShown by remember { mutableStateOf(0) }
    var pastReservationsShown by remember { mutableStateOf(0) }

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadWorkerReservations()
        }
    }

    // Initialize the shown reservation counts based on the available reservations
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
                    ReservationCard(reservation)
                }
            } else {
                item {
                    NoReservationsMessage()
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
                    }else{

                    }
                }
            }
        }

        item {
            Text("Reservas anteriores", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        }
        pastReservations?.let { pastReservations ->
            items(pastReservations.take(pastReservationsShown)) { reservation ->
                ReservationCard(reservation)
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
fun ReservationCard(reservation: ReservationEntity) {
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
                Text("Pelitos Locos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Servicio: ${reservation.service?.name}")
                Text("Cliente: ${reservation.client?.name}")
                Text("Precio: ${reservation.service?.price}")
                Text("Día: ${reservation.initDate?.toDate()}")
                reservation.establishment?.address?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
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
fun NoReservationsMessage() {
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

