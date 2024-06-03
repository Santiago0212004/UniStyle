package com.example.unistylejc.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unistylejc.R

import com.example.unistylejc.viewmodel.LogInViewmodel
import edu.co.icesi.unistyle.domain.model.AppAuthState

@Composable
fun LoginScreen(navController: NavHostController, loginViewModel: LogInViewmodel = viewModel()){
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(230.dp)
                .padding(top = 76.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bienvenido!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Ingrese correo") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Ingrese contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                loginViewModel.login(email,password)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0xFF9C27B0))
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¿No tienes una cuenta?",
            modifier = Modifier.clickable {
                navController.navigate("signup")
            },
            color = Color(0xFF9C27B0),
            fontStyle = FontStyle.Italic
        )
    }

    val authState = loginViewModel.authStatus.observeAsState()

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AppAuthState.Loading -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AppAuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AppAuthState.Success -> {
                Toast.makeText(context, "Bienvenido ${state.userID}", Toast.LENGTH_LONG).show()
            }
            is AppAuthState.SuccessLogin -> {
                Toast.makeText(context, "Bienvenido ${state.userID}", Toast.LENGTH_LONG).show()
                /*val intent = when (state.role) {
                    "worker" -> Intent(context, WorkerSelectActivity::class.java)
                    "customer" -> Intent(context, MainCustomerActivity::class.java)
                    else -> null
                }
                intent?.let { context.startActivity(it) }*/
            }
            else -> Unit
        }
    }
}

