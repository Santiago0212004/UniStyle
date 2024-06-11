package com.example.unistylejc.screens

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.WorkerChangePasswordViewmodel
import com.example.unistylejc.viewmodel.WorkerProfileViewModel
import com.example.unistylejc.viewmodel.WorkerUpdateProfileViewmodel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.delay

@Composable
private fun ScreenContent(navController: NavHostController, userState: Worker?, viewModel: WorkerChangePasswordViewmodel) {
    val context = LocalContext.current
    var newPassword by remember { mutableStateOf("") }
    var newPasswordConfirmation by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }
    var passwordsMatch by remember { mutableStateOf(true) }

    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.authStatus.observeAsState()

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AppAuthState.Loading -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AppAuthState.Error -> {
                showErrorDialog = true
            }
            is AppAuthState.Success -> {
            }
            is AppAuthState.SuccessLogin -> {
                viewModel.changePassword(newPassword)
                showDialog = true
            }
            else -> Unit
        }
    }

    if (showDialog) {
        MinimalDialog(onDismissRequest = { showDialog = false })
        LaunchedEffect(Unit) {
            delay(3000L) // Delay for 3 seconds
            showDialog = false
            shouldNavigate = true
        }
    }

    if (showErrorDialog) {
        MinimalErrorDialog(onDismissRequest = { showDialog = false })
        LaunchedEffect(Unit) {
            delay(3000L) // Delay for 3 seconds
            showErrorDialog = false
            shouldNavigate = true
        }
    }

    if (shouldNavigate) {
        LaunchedEffect(Unit) {
            navController.navigate("worker/profile")
            shouldNavigate = false
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navController.navigate("worker/settings") },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Mi perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        ProfileSection(navController, userState)

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextFieldBackground(Color(0xFFEA8D1F)) {
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    passwordsMatch = newPassword == newPasswordConfirmation // Update passwordsMatch
                },
                label = { Text("Nueva contraseña") },
                placeholder = { Text("Ingrese su nueva contraseña") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
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
                    focusedPlaceholderColor = Color.White
                ),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = if (newPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                }
            )
        }

        OutlinedTextFieldBackground(Color(0xFFEA8D1F)) {
            OutlinedTextField(
                value = newPasswordConfirmation,
                onValueChange = {
                    newPasswordConfirmation = it
                    passwordsMatch = newPassword == newPasswordConfirmation // Update passwordsMatch
                },
                label = { Text("Confirme su nueva contraseña") },
                placeholder = { Text("Confirme su nueva contraseña") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
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
                    focusedPlaceholderColor = Color.White
                ),
                visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmNewPasswordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = if (confirmNewPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                }
            )
        }

        // Display red text if passwords do not match
        if (!passwordsMatch) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextFieldBackground(Color(0xFFEA8D1F)) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña actual") },
                placeholder = { Text("Ingrese su contraseña actual") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
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
                    focusedPlaceholderColor = Color.White
                ),
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (currentPasswordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = if (currentPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (newPassword.isNotEmpty() && newPasswordConfirmation.isNotEmpty() && password.isNotEmpty()) {
                    if (newPassword == newPasswordConfirmation) {
                        viewModel.reauthenticate(password)
                    } else {
                        passwordsMatch = false // Set passwordsMatch to false if passwords do not match
                    }
                } else {
                    navController.navigate("worker/profile")
                }
            },
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
private fun OutlinedTextFieldBackground(
    color: Color,
    content: @Composable () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .background(
                    color.copy(alpha = 0.5f),

                    shape = RoundedCornerShape(4.dp)
                )
        )
        content()
    }
}

@Composable
private fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
                Text(
                    text = "Se realizaron los cambios",
                    modifier = Modifier
                        .fillMaxSize()
                        .size(36.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}
@Composable
private fun MinimalErrorDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center, // Center the content vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center the content horizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_circle),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp)) // Add space between the icon and the text
                Text(
                    text = "No se pudo realizar el cambio de contraseña", // Fixed the typo here as well
                    modifier = Modifier
                        .fillMaxWidth(), // Ensure the text takes the full width available
                    textAlign = TextAlign.Center,
                )
            }
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
fun WorkerChangePasswordScreen(navController: NavHostController, viewModel: WorkerChangePasswordViewmodel = viewModel()) {
    val isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    val userState by viewModel.userState.observeAsState()

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadUser()
            viewModel.observeUser();
        }

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ScreenContent(navController, userState, viewModel)
            }
        }
    }
}

