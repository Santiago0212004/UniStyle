package com.example.unistylejc.screens.customerEstablishment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.screens.resources.RatingStars
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationSection(navController: NavHostController, viewModel: CustomerEstablishmentViewModel) {

    val selectedWorker by viewModel.selectedWorker.observeAsState()
    val workers by viewModel.workers.observeAsState()
    val establishment by viewModel.establishment.observeAsState()
    var showReservationDialog by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(16.dp))

    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        val workersScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .horizontalScroll(workersScrollState)
        ) {
            if (workers?.isNotEmpty() == true) {
                workers!!.forEach { worker ->
                    if (worker != null) {
                        WorkerThumbnail(worker, onSelect = { viewModel.loadWorker(it.id) })
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
                WorkerInfoCard(worker = worker, onReserveClick = { showReservationDialog = true })
            }
        } ?: run {
            Text(text = "Seleccione un trabajador", style = MaterialTheme.typography.bodyLarge)
        }

        if (showReservationDialog) {
            selectedWorker?.let {
                establishment?.let {
                    navController.navigate("reserve/${establishment!!.id}/${selectedWorker!!.id}")
                }
            }
        }
    }

}

@Composable
fun WorkerThumbnail(worker: Worker, onSelect: (Worker) -> Unit) {
    worker.let {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onSelect(it) }
        ) {
            Image(
                painter = rememberAsyncImagePainter(it.picture),
                contentDescription = "Worker profile pic",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(64.dp)
                    .background(Color.Gray)
            )
            Text(text = it.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun WorkerInfoCard(worker: Worker, onReserveClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "¡Te atenderá ${worker.name}!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = worker.description,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = rememberAsyncImagePainter(model = worker.picture),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        RatingStars(worker.score)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onReserveClick, modifier = Modifier.align(Alignment.End)) {
            Text(text = "Reservar")
        }
    }
}
