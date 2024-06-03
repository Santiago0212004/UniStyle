package com.example.unistylejc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.viewmodel.SignUpViewmodel
import edu.co.icesi.unistyle.domain.model.AppAuthState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.EstablishmentViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController, viewModel: SignUpViewmodel = viewModel(), viewmodelEst: EstablishmentViewmodel = viewModel()) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isWorker by remember { mutableStateOf(false) }
    var selectedEstablishment by remember { mutableStateOf<Establishment?>(null) }

    val establishments by viewmodelEst.establishmentState.observeAsState(initial = listOf())
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(isWorker) {
        if (isWorker) {
            viewmodelEst.loadEstablishmentList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Registro", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "¿Eres trabajador?")
            Switch(
                checked = isWorker,
                onCheckedChange = { isWorker = it }
            )
        }

        if (isWorker) {
            Text(text = "Establecimiento asociado:", modifier = Modifier.padding(vertical = 8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedEstablishment?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Establecimiento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { expanded = true }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    establishments?.forEach { establishment ->
                        DropdownMenuItem(
                            text = {
                                if (establishment != null) {
                                    Text(text = establishment.name)
                                }
                            },
                            onClick = {
                                selectedEstablishment = establishment
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (!isWorker) {
                    val customer = Customer("", email, name, username, "", null, null)
                    viewModel.signupUser(customer, password)
                } else {
                    val worker = Worker("", email, name, username, "", null, null, selectedEstablishment?.id ?: "", null)
                    viewModel.signupWorker(worker, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Crear cuenta")
        }

        Button(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Ya tengo cuenta")
        }

        val authState = viewModel.authStatus.observeAsState()

        LaunchedEffect(authState.value) {
            when (val state = authState.value) {
                is AppAuthState.Loading -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                is AppAuthState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                is AppAuthState.Success -> {
                    Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                }
                is AppAuthState.SuccessLogin -> {
                    Toast.makeText(context, "Bienvenido ${state.userID}", Toast.LENGTH_LONG).show()
                    when (state.role) {
                        "worker" -> navController.navigate("worker/main")
                        "customer" -> navController.navigate("customer/main")
                    }
                }
                else -> {}
            }
        }
    }
}