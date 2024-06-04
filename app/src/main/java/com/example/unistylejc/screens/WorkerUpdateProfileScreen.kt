package com.example.unistylejc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.WorkerProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
private fun ScreenContent(navController: NavHostController, userState: Worker?) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(userState) {
        userState?.let {
            name = it.name
            username = it.username
            email = it.email
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Mi perfil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ProfileSection(navController, userState)

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextFieldBackground(Color(0xFFEA8D1F)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                placeholder = { Text("Ingrese su nombre") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    unfocusedPlaceholderColor = Color.White,
                    unfocusedLeadingIconColor = Color.Black,
                    focusedTextColor = Color.White,
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    focusedLeadingIconColor = Color.Black,
                    focusedPlaceholderColor = Color.White),
            )
        }


        OutlinedTextFieldBackground(Color(0xFFEA8D1F)) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                placeholder = { Text("Ingrese su nombre de usuario") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    unfocusedPlaceholderColor = Color.White,
                    unfocusedLeadingIconColor = Color.Black,
                    focusedTextColor = Color.White,
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    focusedLeadingIconColor = Color.Black,
                    focusedPlaceholderColor = Color.White),
            )
        }
        OutlinedTextFieldBackground(Color(0xFFEA8D1F)) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Ingrese contraseña") },
                placeholder = { Text("Ingrese su contraseña") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.White,
                    unfocusedPlaceholderColor = Color.White,
                    unfocusedLeadingIconColor = Color.Black,
                    focusedTextColor = Color.White,
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    focusedLeadingIconColor = Color.Black,
                    focusedPlaceholderColor = Color.White),
            )

        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5D16A6)
            ),
            contentPadding = PaddingValues(16.dp),
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "Confirmar cambios", fontSize = 16.sp, color = Color.White)
        }
    }
}




@Composable
private fun ProfileSection(navController: NavHostController, userState: Worker?) {
    Box(
        modifier = Modifier
            .size(width = 400.dp, height = 230.dp)
            .background(color = Color(0xFFD4BEEB), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(userState?.picture),
                contentDescription = "Imagen de perfil",
                modifier = Modifier
                    .size(124.dp)
                    .clip(CircleShape)
                    .clickable { navController.navigate("worker/settings/updateProfile") },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(width = 220.dp, height = 124.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(16.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = userState?.name ?: "",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userState?.email ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun WorkerUpdateProfileScreen(navController: NavHostController, viewModel: WorkerProfileViewModel = viewModel()) {
    val isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    val userState by viewModel.userState.observeAsState()

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadUser()
        }

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ScreenContent(navController, userState)
            }
        }
    }
}
