package com.example.unistylejc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel

@Composable
fun CustomerEstablishmentScreen(navController: NavHostController, establishmentId: String, viewModel: CustomerEstablishmentViewModel = viewModel()) {
    val establishment by viewModel.establishment.observeAsState()
    var inReservation by remember { mutableStateOf(true) }
    var inComments by remember { mutableStateOf(false) }
    var inPhotos by remember { mutableStateOf(false) }
    val selectedWorker by viewModel.selectedWorker.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEstablishment(establishmentId)
    }

    establishment?.let { est ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(model = est.picture),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = est.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${est.address}, ${est.city}", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (inReservation) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Reservas")
                    }
                } else {
                    OutlinedButton(
                        onClick = { inReservation = true; inComments = false; inPhotos = false; },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Reservas")
                    }
                }

                if (inComments) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Comentarios")
                    }
                } else {
                    OutlinedButton(
                        onClick = { inReservation = false; inComments = true; inPhotos = false; },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Comentarios")
                    }
                }

                if (inPhotos) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Fotos")
                    }
                } else {
                    OutlinedButton(
                        onClick = { inReservation = false; inComments = false; inPhotos = true; },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Fotos")
                    }
                }
            }

            if (inReservation) {
                ReservationSection(viewModel, est, selectedWorker)
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ReservationSection(viewModel: CustomerEstablishmentViewModel, establishment: Establishment, selectedWorker: Worker?) {
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
                Button(onClick = { /* Handle reservation */ }, modifier = Modifier.align(Alignment.End)) {
                    Text(text = "Reservar")
                }
            }
        }
    } ?: run {
        Text(text = "Seleccione un trabajador", style = MaterialTheme.typography.bodyLarge)
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
