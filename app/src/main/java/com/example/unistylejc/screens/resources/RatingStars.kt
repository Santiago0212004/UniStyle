package com.example.unistylejc.screens.resources

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R

@Composable
fun RatingStars(score: Double) {
    Row {
        repeat(5) { index ->
            val starPainter = if (index < score) {
                rememberAsyncImagePainter(model = R.drawable.ic_star_filled)
            } else {
                rememberAsyncImagePainter(model = R.drawable.ic_star_empty)
            }
            Image(
                painter = starPainter,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(text = score.toString(), style = MaterialTheme.typography.bodySmall)
    }
}