package com.example.unistylejc.screens.resources

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
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
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RatingStars(score: Double) {
    val decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
    val decimalFormat = DecimalFormat("#.#", decimalFormatSymbols)
    val rScore = decimalFormat.format(score).toDouble()
    val roundedScore = rScore.roundToInt()

    Row {
        repeat(5) { index ->
            val starPainter = if ((index+1) <= roundedScore) {
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
        Text(text = rScore.toString(), style = MaterialTheme.typography.bodySmall)
    }
}