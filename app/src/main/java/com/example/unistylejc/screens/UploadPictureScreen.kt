package com.example.unistylejc.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.viewmodel.UploadImageViewModel

@Composable
fun UploadPictureScreen(navController: NavHostController, viewModel: UploadImageViewModel = viewModel()) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val currentRole by viewModel.currentRole.observeAsState()
    val currentUser by viewModel.currentUser.observeAsState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                color = Color(0xFFFFC0CB),
                center = Offset(x = 0.1f * canvasWidth, y = 0.1f * canvasHeight),
                radius = 0.2f * canvasWidth
            )

            drawCircle(
                color = Color(0xFFA7A7FF),
                center = Offset(x = 0.9f * canvasWidth, y = 0.3f * canvasHeight),
                radius = 0.15f * canvasWidth
            )

            drawCircle(
                color = Color(0xFFFFE1B5),
                center = Offset(x = 0.2f * canvasWidth, y = 0.9f * canvasHeight),
                radius = 0.25f * canvasWidth
            )

            drawCircle(
                color = Color(0xFFE4C2FF),
                center = Offset(x = 0.8f * canvasWidth, y = 0.8f * canvasHeight),
                radius = 0.1f * canvasWidth
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sube tu foto de perfil!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        imageUri.value?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    imageUri.value?.let { uri ->
                        val isWorker = currentRole == "worker"
                        viewModel.uploadProfilePicture(uri, isWorker) { success ->
                            if (success) {
                                when (currentRole) {
                                    "worker" -> navController.navigate("worker/community")
                                    "customer" -> navController.navigate("customer/discover")
                                }
                            } else {
                                Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            ) {
                Text("Subir Imagen")
            }
        } ?: run {
            Button(
                onClick = {
                    pickImageLauncher.launch("image/*")
                }
            ) {
                Text("Seleccionar Imagen")
            }
        }
        LaunchedEffect(Unit) {
            viewModel.getCurrentUser()
            currentUser?.let {
                viewModel.getRole(it.uid)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                when (currentRole) {
                    "worker" -> navController.navigate("worker/community")
                    "customer" -> navController.navigate("customer/discover")
                    else -> Toast.makeText(context, "No se pudo determinar el rol", Toast.LENGTH_LONG).show()
                }
            }
        ) {
            Text("No subir imagen")
        }
    }
}

