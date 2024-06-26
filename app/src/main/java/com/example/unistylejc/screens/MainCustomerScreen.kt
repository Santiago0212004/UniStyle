package com.example.unistylejc.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.screens.resources.RatingStars
import com.example.unistylejc.viewmodel.MainCustomerViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCustomerScreen(navController: NavHostController, viewModel: MainCustomerViewModel = viewModel()) {
    val context = LocalContext.current
    var isDialogOpen by remember { mutableStateOf(false) }
    var isMapView by remember { mutableStateOf(false) }
    var sortOptionsExpanded by remember { mutableStateOf(false) }

    val loggedCustomer by viewModel.loggedCustomer.observeAsState()
    val establishments by viewModel.establishments.observeAsState(emptyList())
    val allEstablishments by viewModel.allEstablishments.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedSortOption by remember { mutableStateOf("Alfabético") }
    val cities = allEstablishments?.mapNotNull { it?.city }?.distinct()
    val categories = allEstablishments?.mapNotNull { it?.category }?.distinct()
    val sortOptions = listOf("Alfabético", "Por puntuación")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            loggedCustomer?.let { customer ->
                Text(text = customer.username, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(4.dp))
                val defaultImg = "https://firebasestorage.googleapis.com/v0/b/unistyle-940e2.appspot.com/o/user.png?alt=media&token=1b93b86f-5718-4a71-8fe8-e7f5dd198a72"
                val imgUrl = if (customer.picture!!.isEmpty()) defaultImg else customer.picture
                Image(
                    painter = rememberAsyncImagePainter(imgUrl),
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
                    viewModel.filterEstablishments(searchQuery, selectedCity, selectedCategory)
                },
                label = { Text("Busca el mejor lugar!") },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
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
            if (!isMapView) {
                Button(
                    onClick = { isMapView = false },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Lista")
                }
                OutlinedButton(
                    onClick = { isMapView = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Mapa")
                }
            } else {
                OutlinedButton(
                    onClick = { isMapView = false },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Lista")
                }
                Button(
                    onClick = { isMapView = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Mapa")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (establishments?.isEmpty() == true) {
            Text(text = "No se encontraron establecimientos.", style = MaterialTheme.typography.bodyLarge)
        } else {
            if (isMapView) {
                establishments?.let { EstablishmentsMap(it, navController) }
            } else {
                Column (modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = sortOptionsExpanded,
                        onExpandedChange = { sortOptionsExpanded = !sortOptionsExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSortOption,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ordenar por") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortOptionsExpanded) },
                            modifier = Modifier
                                .weight(1f)
                                .menuAnchor()
                                .fillMaxWidth()
                                .clickable { sortOptionsExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = sortOptionsExpanded,
                            onDismissRequest = { sortOptionsExpanded = false }
                        ) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        selectedSortOption = option
                                        sortOptionsExpanded = false
                                        viewModel.sortEstablishments(option)
                                    },
                                )
                            }
                        }
                    }
                }
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    establishments?.forEach { establishment ->
                        EstablishmentCard(establishment, navController)
                    }
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
            establishments?.let {
                viewModel.sortEstablishments(selectedSortOption)
            }
        } else {
            Toast.makeText(context, "No se ha autenticado ningún usuario", Toast.LENGTH_LONG).show()
        }
    }

    cities?.let { it ->
        categories?.let { it1 ->
            if (isDialogOpen) {
                FiltersDialog(
                    onDismissRequest = { isDialogOpen = false },
                    viewModel = viewModel,
                    searchQuery = searchQuery,
                    selectedCity = selectedCity,
                    selectedCategory = selectedCategory,
                    onCitySelected = { selectedCity = it },
                    onCategorySelected = {selectedCategory = it},
                    cities = it,
                    categories = it1
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDialog(
    onDismissRequest: () -> Unit,
    viewModel: MainCustomerViewModel,
    searchQuery: String,
    selectedCity: String,
    selectedCategory: String,
    onCitySelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    cities: List<String>,
    categories: List<String>
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White),
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        content = {
            var citiesExpanded by remember { mutableStateOf(false) }
            var localSelectedCity by remember { mutableStateOf(selectedCity) }

            var categoriesExpanded by remember { mutableStateOf(false) }
            var localSelectedCategory by remember { mutableStateOf(selectedCategory) }

            Column (
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Filtros", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = citiesExpanded,
                    onExpandedChange = { citiesExpanded = !citiesExpanded }
                ) {
                    OutlinedTextField(
                        value = localSelectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona una ciudad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = citiesExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { citiesExpanded = true }
                    )
                    ExposedDropdownMenu(
                        expanded = citiesExpanded,
                        onDismissRequest = { citiesExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "TODAS LAS CIUDADES") },
                            onClick = {
                                localSelectedCity = "TODAS LAS CIUDADES"
                                citiesExpanded = false
                            }
                        )
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(text = city) },
                                onClick = {
                                    localSelectedCity = city
                                    citiesExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = categoriesExpanded,
                    onExpandedChange = { categoriesExpanded = !categoriesExpanded }
                ) {
                    OutlinedTextField(
                        value = localSelectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona una categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriesExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { categoriesExpanded = true }
                    )
                    ExposedDropdownMenu(
                        expanded = categoriesExpanded,
                        onDismissRequest = { categoriesExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "TODAS LAS CATEGORIAS") },
                            onClick = {
                                localSelectedCategory = "TODAS LAS CATEGORIAS"
                                categoriesExpanded = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category) },
                                onClick = {
                                    localSelectedCategory = category
                                    categoriesExpanded = false
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
                        viewModel.filterEstablishments(searchQuery, localSelectedCity, localSelectedCategory)
                        onCitySelected(localSelectedCity)
                        onCategorySelected(localSelectedCategory)
                    }) {
                        Text("Aplicar filtros")
                    }
                }
            }
        }
    )
}

@Composable
fun EstablishmentCard(establishment: Establishment?, navController: NavHostController) {
    establishment?.let {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { navController.navigate("establishmentDetail/${establishment.id}") },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                Column (
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(text = it.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingStars(it.score)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it.city, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it.category, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it.address, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it.email, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.padding(end = 16.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(it.picture),
                        contentDescription = "Establishment pic",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable {}
                    )
                }
            }
        }
    }
}

@Composable
fun EstablishmentsMap(establishments: List<Establishment?>, navController: NavHostController) {
    if (establishments.isEmpty()) {
        Text(text = "No se encontraron establecimientos en el mapa.", style = MaterialTheme.typography.bodyLarge)
        return
    }

    val initialPosition = LatLng(-34.0, 151.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    val selectedEstablishment = remember { mutableStateOf<Establishment?>(null) }
    var navigateToEstablishment by remember { mutableStateOf<Establishment?>(null) }

    key(establishments) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            cameraPositionState = cameraPositionState
        ) {
            establishments.forEach { establishment ->
                establishment?.let {
                    Marker(
                        state = rememberMarkerState(
                            position = LatLng(it.latitude, it.longitude)
                        ),
                        title = it.name,
                        snippet = it.address,
                        onClick = { marker ->
                            selectedEstablishment.value = it
                            marker.showInfoWindow()
                            true
                        },
                        onInfoWindowClick = {
                            navigateToEstablishment = selectedEstablishment.value
                        }
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
    navigateToEstablishment?.let { establishment ->
        LaunchedEffect(establishment) {
            navController.navigate("establishmentDetail/${establishment.id}")
            navigateToEstablishment = null
        }
    }
}