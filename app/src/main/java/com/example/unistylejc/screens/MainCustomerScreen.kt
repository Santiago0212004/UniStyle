package com.example.unistylejc.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.viewmodel.MainCustomerViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainCustomerScreen(navController: NavHostController, viewModel: MainCustomerViewModel = viewModel()) {
    val context = LocalContext.current

    val loggedCustomer by viewModel.loggedCustomer.observeAsState()
    val establishments by viewModel.establishments.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.filterEstablishments(it)
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        loggedCustomer?.let { customer ->
            Text(text = customer.name)
        } ?: Text(text = "ERROR")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(establishments) { establishment ->
                EstablishmentCard(establishment)
            }
        }
    }

    LaunchedEffect (Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val customerId = currentUser.uid
            viewModel.getLoggedCustomer(customerId)
            viewModel.getEstablishments()
        } else {
            Toast.makeText(context, "No se ha autenticado ningún usuario", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun EstablishmentCard(establishment: Establishment?){
    establishment?.let {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = it.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it.city, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it.address, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
