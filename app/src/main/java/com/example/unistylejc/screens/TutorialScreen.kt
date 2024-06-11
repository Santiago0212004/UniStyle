package com.example.unistylejc.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.unistylejc.R
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

fun Context.isFirstTimeLaunch(): Boolean {
    val pref = getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
    return pref.getBoolean("is_first_time", true)
}

fun Context.setFirstTimeLaunch(isFirstTime: Boolean) {
    val pref = getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
    with(pref.edit()) {
        putBoolean("is_first_time", isFirstTime)
        apply()
    }
}

@Composable
fun TutorialScreen(navController: NavHostController) {
    val context = LocalContext.current
    val images = listOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3,
        R.drawable.img4
    )
    val texts = listOf(
        buildAnnotatedString {
            append("Bienvenidos a ")
            withStyle(style = SpanStyle(color = Color(0xFF6200EA))) { append("UniStyle") }
        },
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color(0xFF6200EA))) { append("Descubre") }
            append(" establecimientos que pueden ser de tu interés")
        },
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color(0xFF6200EA))) { append("Reserva") }
            append(" en tu establecimiento de preferencia el servicio que desees")
        },
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color(0xFF6200EA))) { append("Comenta") }
            append(" sobre tu experiencia.\n")
            withStyle(style = SpanStyle(color = Color.Black, fontSize = 20.sp)) { append("Gracias por unirte a nosotros!") }
        }
    )

    var currentPage by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = texts[currentPage],
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        Image(
            painter = painterResource(id = images[currentPage]),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentPage > 0) {
                Button(onClick = { currentPage -= 1 }) {
                    Text("Anterior")
                }
            }else{
                Spacer(modifier = Modifier.weight(1f))
            }
            if (currentPage < images.size - 1) {
                Button(onClick = { currentPage += 1 }, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text("Siguiente")
                }
            } else {
                Button(onClick = {
                    context.setFirstTimeLaunch(false)
                    navController.navigate("customer/discover") {
                        popUpTo("tutorial") { inclusive = true }
                    }
                }) {
                    Text("Finalizar")
                }
            }
        }
    }
}