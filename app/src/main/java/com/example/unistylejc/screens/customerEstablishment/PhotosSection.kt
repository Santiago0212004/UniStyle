package com.example.unistylejc.screens.customerEstablishment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.domain.model.Establishment

@Composable
fun PhotosSection(establishment: Establishment) {
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn(
        modifier = Modifier.wrapContentHeight()
    ) {
        if (establishment.photos.isNotEmpty()) {
            items(establishment.photos) { photoUrl ->
                if (photoUrl.isNotEmpty()) {
                    PhotoCard(photoUrl)
                }
            }
        }
    }
}

@Composable
fun PhotoCard(photoUrl: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            contentDescription = "Establishment photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
