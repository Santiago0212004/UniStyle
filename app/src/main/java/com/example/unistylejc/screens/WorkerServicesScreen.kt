package com.example.unistylejc.screens

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.viewmodel.WorkerServicesViewModel

@Composable
fun WorkerServicesScreen(navController: NavHostController, viewModel: WorkerServicesViewModel = viewModel()){

    val worker by viewModel.loggedWorker.observeAsState()
    val workerServices by viewModel.workerServices.observeAsState()

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
                        .clickable {navController.navigate("worker/profile")}
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(
                text = "Mis servicios",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            ExtendedFloatingActionButton(
                onClick = {/*TODO*/},
                icon = { Icon(imageVector = Icons.Default.Add, "Add service.") },
                text = {Text(text = "Añadir Servicio")},
                containerColor = Color(0xFF5D16A6),
                contentColor = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))



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
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF2E8FC), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
        ){
            Row (Modifier.fillMaxHeight()){
                Column {
                    Text(s.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$"+s.price.toString(), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()){
                    Icon(modifier = Modifier.clickable {  }.size(24.dp), imageVector = Icons.Default.Delete, contentDescription = "Delete",
                        tint = Color.Red)
                }
            }
        }
    }
}