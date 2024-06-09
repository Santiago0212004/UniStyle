package com.example.unistylejc.screens.customerEstablishment

import android.os.Build
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.screens.resources.RatingStars
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomerEstablishmentScreen(navController: NavHostController, establishmentId: String, viewModel: CustomerEstablishmentViewModel = viewModel()) {
    val establishment by viewModel.establishment.observeAsState()
    var inReservation by remember { mutableStateOf(true) }
    var inComments by remember { mutableStateOf(false) }
    var inPhotos by remember { mutableStateOf(false) }
    val loggedCustomer by viewModel.loggedCustomer.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.getLoggedCustomer()
        viewModel.loadEstablishment(establishmentId)
        viewModel.loadEstablishment(establishmentId)
        viewModel.loadEstablishmentWorkers(establishmentId)
        viewModel.loadEstablishmentComments(establishmentId)
    }

    establishment?.let { est ->
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.Cyan,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(est.picture),
                        contentDescription = "Establishment image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    val descriptionScrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = est.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${est.address}, ${est.city}, ${est.state}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 64.dp)
                                .verticalScroll(descriptionScrollState)
                        ) {
                            Text(
                                text = est.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        RatingStars(score = est.score)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    if (inReservation) {
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Reservas",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { inReservation = true; inComments = false; inPhotos = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Reservas",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    if (inComments) {
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Opiniones",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { inReservation = false; inComments = true; inPhotos = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Opiniones",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    if (inPhotos) {
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Fotos",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { inReservation = false; inComments = false; inPhotos = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Fotos",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }



            if (inReservation) {
                ReservationSection(navController,viewModel)
            }
            if (inComments) {
                CommentsSection(viewModel)
            }
            if (inPhotos) {
                PhotosSection(viewModel)
            }


        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
