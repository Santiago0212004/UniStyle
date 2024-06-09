package com.example.unistylejc.screens.customerEstablishment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.screens.resources.RatingStars
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentsSection(
    viewModel: CustomerEstablishmentViewModel
) {
    val showDialog = remember { mutableStateOf(false) }
    val workers by viewModel.workers.observeAsState()
    val comments by viewModel.comments.observeAsState()

    if(workers?.isNotEmpty() == true && showDialog.value){
        AddCommentDialog(
            viewModel = viewModel,
            onDismissRequest = { showDialog.value = false },
            onConfirmRequest = {
                viewModel.addComment(it)
                showDialog.value = false
            }
        )
    }


    Column {
        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Añadir Comentario")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .verticalScroll(scrollState)
        ) {
            if (comments?.isNotEmpty() == true) {
                comments!!.forEach{comment ->
                    comment?.let {
                        CommentCard(viewModel, comment)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(viewModel: CustomerEstablishmentViewModel, comment: Comment) {
    comment.let {
        val worker by produceState<Worker?>(initialValue = null) {
            value = viewModel.findWorkerById(comment.workerRef)
        }

        val customer by produceState<Customer?>(initialValue = null) {
            value = viewModel.findCustomerById(comment.customerRef)
        }

        if (worker != null && customer != null) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(customer!!.picture),
                            contentDescription = "Customer profile pic",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(60.dp)
                                .background(Color(0xFF9C2DB))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = customer!!.name, style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    comment.date?.toDate()?.let {
                        val calendar = Calendar.getInstance()
                        calendar.time = it
                        calendar.add(Calendar.HOUR_OF_DAY, -5)
                        val adjustedDate = calendar.time

                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        val date = adjustedDate.let { d -> sdf.format(d) } ?: "Unknown date"

                        Text(text = date, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = comment.content, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(model = worker!!.picture),
                            contentDescription = "Worker profile pic",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 8.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(text = "Servicio otorgado por: ${worker!!.name}", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    RatingStars(comment!!.score)

                }
            }
        }
    }
}