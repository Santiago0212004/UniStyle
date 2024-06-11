package com.example.unistylejc.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.unistylejc.R
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@Composable
fun InformationScreen(navController: NavHostController) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenContent(navController)
        }
    }
}
@Composable
private fun ScreenContent(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack()},
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(Color.White)) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Acerca de nosotros",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Image(
            painter = rememberAsyncImagePainter(R.drawable.logo),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0BBE4), shape = RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text(fontSize = 14.sp,
                text = "Nuestra aplicación está diseñada para ser el destino único donde puedes descubrir y reservar servicios de belleza y cuidado personal con facilidad y confianza.\n\n" +
                        "Creemos que cada persona merece sentirse y verse bien, y sabemos lo importante que es encontrar un profesional de confianza para satisfacer tus necesidades de estilo. \n" +
                        "\n " +
                        "Por eso, hemos reunido una amplia variedad de peluquerías, barberías y otros servicios relacionados, todo en un solo lugar.",
                color = Color.Black
            )
        }
    }
}

