package com.example.unistylejc.screens.customerEstablishment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import com.google.firebase.Timestamp
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentDialog(
    viewModel: CustomerEstablishmentViewModel,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (Comment) -> Unit
) {
    val loggedCustomer by viewModel.loggedCustomer.observeAsState()
    val workers by viewModel.workers.observeAsState()
    val establishment by viewModel.establishment.observeAsState()

    var selectedWorker by remember { mutableStateOf<Worker?>(null) }
    var commentContent by remember { mutableStateOf("") }
    var commentScore by remember { mutableDoubleStateOf(0.0) }

    var workerMenuExpanded by remember { mutableStateOf(false) }


    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        content = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Añadir Comentario", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Seleccionar Trabajador", style = MaterialTheme.typography.bodyLarge)
                    ExposedDropdownMenuBox(
                        expanded = workerMenuExpanded,
                        onExpandedChange = { workerMenuExpanded = !workerMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedWorker?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Trabajador") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = workerMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { workerMenuExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = workerMenuExpanded,
                            onDismissRequest = { workerMenuExpanded = false }
                        ) {
                            workers?.forEach { worker ->
                                DropdownMenuItem(
                                    text = {
                                        if (worker != null) {
                                            Text(text = worker.name)
                                        }
                                    },
                                    onClick = {
                                        selectedWorker = worker
                                        workerMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Comentario", style = MaterialTheme.typography.bodyLarge)
                    OutlinedTextField(
                        value = commentContent,
                        onValueChange = { commentContent = it },
                        label = { Text("Escribe tu comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Calificación", style = MaterialTheme.typography.bodyLarge)
                    RatingBar(
                        rating = commentScore,
                        onRatingChanged = { newRating -> commentScore = newRating }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDismissRequest() }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            if (commentContent.isNotEmpty() && selectedWorker != null && loggedCustomer != null) {
                                try {
                                    val comment = establishment?.let {
                                        Comment(
                                            id = UUID.randomUUID().toString(),
                                            content = commentContent,
                                            date = Timestamp.now(),
                                            score = commentScore,
                                            customerRef = loggedCustomer!!.id,
                                            workerRef = selectedWorker!!.id,
                                            establishmentRef = it.id
                                        )
                                    }
                                    comment?.let{
                                        onConfirmRequest(it)
                                    }
                                    onDismissRequest()
                                } catch (e: Exception) {
                                    Log.e("AddCommentDialog", "Error creating comment: ${e.message}")
                                }
                            } else {
                                Log.e("AddCommentDialog", "Required fields are missing: commentContent=$commentContent, selectedWorker=$selectedWorker, loggedCustomer=$loggedCustomer")
                            }
                        }) {
                            Text("Añadir")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun RatingBar(
    rating: Double,
    onRatingChanged: (Double) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 5
) {
    Row(modifier = modifier) {
        for (i in 1..starCount) {
            val scale by animateFloatAsState(
                targetValue = if (i <= rating) 1.2f else 1.0f,
                label = ""
            )
            Image(
                painter = painterResource(id = if (i <= rating) R.drawable.ic_star_filled else R.drawable.ic_star_empty),
                contentDescription = null,
                modifier = Modifier
                    .size((24 * scale).dp)
                    .clickable { onRatingChanged(i.toDouble()) }
            )
        }
    }
}
