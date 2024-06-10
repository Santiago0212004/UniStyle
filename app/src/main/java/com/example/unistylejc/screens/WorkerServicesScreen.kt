package com.example.unistylejc.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.screens.customerEstablishment.RatingBar
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import com.example.unistylejc.viewmodel.WorkerServicesViewModel
import com.google.firebase.Timestamp
import java.util.UUID

@Composable
fun WorkerServicesScreen(naController: NavHostController, viewModel: WorkerServicesViewModel = viewModel()){

    val worker by viewModel.loggedWorker.observeAsState()
    val workerServices by viewModel.workerServices.observeAsState()
    val showAddServiceDialog = remember {mutableStateOf(false)}

    LaunchedEffect(Unit) {
        viewModel.getLoggedWorker()
    }

    worker?.let {
        viewModel.loadWorkerServices()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            worker?.let { w ->
                Text(text = w.username, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = rememberAsyncImagePainter(w.picture),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .clickable {}
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(
                text = "Mis servicios",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {showAddServiceDialog.value = true},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Añadir Servicio")
        }

        if(showAddServiceDialog.value){
            AddServiceDialog(
                onDismissRequest = {showAddServiceDialog.value = false},
                onConfirmRequest = { name, price ->
                    showAddServiceDialog.value = false
                    worker?.let {
                        viewModel.addServiceToWorker(name, price)
                    }
                }
            )
        }

        workerServices?.let {

            if(it.isEmpty()){
                Text("No ofrece ningun servicio por ahora.")
            } else {
                val servicesScroll = rememberScrollState()
                Column (modifier = Modifier.verticalScroll(servicesScroll)) {
                    it.forEach{ service ->
                        ServiceCard(service)
                    }
                }
            }
        }
    }


}

@Composable
fun ServiceCard(service: Service?){
    service?.let { s ->
        Row {
            Column {
                Text(s.name)
                Spacer(modifier = Modifier.height(8.dp))
                Text(s.price.toString())
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { /*TODO*/ }) {
                Text("Eliminar")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (String, Double) -> Unit
) {
    var serviceName by remember { mutableStateOf("") }
    var servicePriceText by remember { mutableStateOf("") }
    var servicePrice by remember { mutableDoubleStateOf(0.0) }



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
                    Text(text = "Añadir Servicio", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Nombre", style = MaterialTheme.typography.bodyLarge)
                    OutlinedTextField(
                        value = serviceName,
                        onValueChange = { serviceName = it },
                        label = { Text("Escribe el nombre del servicio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = servicePriceText,
                        onValueChange = {
                            servicePriceText = it
                            servicePrice = it.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Escribe el precio del servicio") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

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
                            if (serviceName.isNotEmpty() && servicePrice > 0.0) {
                                try {
                                    onConfirmRequest(serviceName,servicePrice)
                                } catch (e: Exception) {
                                    Log.e("AddCommentDialog", "Error creating service: ${e.message}")
                                }
                            } else {
                                Log.e("AddCommentDialog", "Required fields are missing")
                            }
                        }) {
                            Text("Añadir")
                        }
                    }
                }
            }
        }
    )
}