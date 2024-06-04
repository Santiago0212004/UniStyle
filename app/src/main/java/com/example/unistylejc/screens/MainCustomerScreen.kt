package com.example.unistylejc.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.viewmodel.MainCustomerViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

@Composable
fun MainCustomerScreen(navController: NavHostController, viewModel: MainCustomerViewModel = viewModel()) {
    val context = LocalContext.current
    var isDialogOpen by remember { mutableStateOf(false) }
    var isMapView by remember { mutableStateOf(false) }

    val loggedCustomer by viewModel.loggedCustomer.observeAsState()
    val establishments by viewModel.establishments.observeAsState(emptyList())
    val allEstablishments by viewModel.allEstablishments.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    val cities = allEstablishments.mapNotNull { it?.city }.distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // Align items to the start (left)
        ) {
            loggedCustomer?.let { customer ->
                Image(
                    painter = rememberAsyncImagePainter(customer.picture),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .clickable {}
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.filterEstablishments(searchQuery, selectedCity)
                },
                label = { Text("Busca el mejor lugar!") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { isDialogOpen = true },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Filtros")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { isMapView = false }) {
                Text("Lista")
            }
            Button(onClick = { isMapView = true }) {
                Text("Mapa")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isMapView) {
            EstablishmentsMap(establishments)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(establishments) { establishment ->
                    EstablishmentCard(establishment)
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val customerId = currentUser.uid
            viewModel.getLoggedCustomer(customerId)
            viewModel.getEstablishments()
        } else {
            Toast.makeText(context, "No se ha autenticado ningún usuario", Toast.LENGTH_LONG).show()
        }
    }

    if (isDialogOpen) {
        FiltersDialog(
            isDialogOpen = isDialogOpen,
            onDismissRequest = { isDialogOpen = false },
            viewModel = viewModel,
            searchQuery = searchQuery,
            selectedCity = selectedCity,
            onCitySelected = { selectedCity = it },
            cities = cities
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDialog(
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: MainCustomerViewModel,
    searchQuery: String,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    cities: List<String>
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White),
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        content = {
            var expanded by remember { mutableStateOf(false) }
            var localSelectedCity by remember { mutableStateOf(selectedCity) }

            Column (
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Filtros", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = localSelectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona una ciudad") },
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
                        DropdownMenuItem(
                            text = { Text(text = "TODAS LAS CIUDADES") },
                            onClick = {
                                localSelectedCity = "TODAS LAS CIUDADES"
                                expanded = false
                            }
                        )
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(text = city) },
                                onClick = {
                                    localSelectedCity = city
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        onDismissRequest()
                        viewModel.filterEstablishments(searchQuery, localSelectedCity)
                        onCitySelected(localSelectedCity)
                    }) {
                        Text("Aplicar filtros")
                    }
                }
            }
        }
    )
}

@Composable
fun EstablishmentCard(establishment: Establishment?) {
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

@Composable
fun EstablishmentsMap(establishments: List<Establishment?>) {
    val initialPosition = LatLng(-34.0, 151.0) // Default location, you can change it as needed
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    // Using a key to force recomposition when the list of establishments changes
    key(establishments) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            establishments.forEach { establishment ->
                establishment?.let {
                    Marker(
                        state = rememberMarkerState(
                            position = LatLng(it.latitude, it.longitude)
                        ),
                        title = it.name,
                        snippet = it.address
                    )
                }
            }
        }

        LaunchedEffect(establishments) {
            val boundsBuilder = LatLngBounds.builder()
            establishments.forEach { establishment ->
                establishment?.let {
                    boundsBuilder.include(LatLng(it.latitude, it.longitude))
                }
            }
            val bounds = boundsBuilder.build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }
}

