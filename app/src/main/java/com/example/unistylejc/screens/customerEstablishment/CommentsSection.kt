package com.example.unistylejc.screens.customerEstablishment

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.screens.resources.RatingStars
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentsSection (viewModel: CustomerEstablishmentViewModel, establishment: Establishment) {
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn(
        modifier = Modifier.wrapContentHeight()
    ) {
        if (establishment.commentsRef.isNotEmpty()) {
            items(establishment.commentsRef) { commentId ->
                if(commentId != ""){
                    CommentCard(viewModel, commentId = commentId)
                }
            }
        }
    }
}


@Composable
fun CommentCard(viewModel: CustomerEstablishmentViewModel, commentId: String) {

    val comment by produceState<Comment?>(initialValue = null) {
        value = viewModel.findCommentById(commentId)
    }

    comment?.let {
        val worker by produceState<Worker?>(initialValue = null) {
            value = viewModel.findWorkerById(comment!!.workerRef)
        }

        val customer by produceState<Customer?>(initialValue = null) {
            value = viewModel.findCustomerById(comment!!.customerRef)
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
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = customer!!.name, style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = comment!!.date?.toDate()?.let { sdf.format(it) } ?: "Unknown date"
                    Text(text = date, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = comment!!.content, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(model = worker!!.picture),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 8.dp),
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