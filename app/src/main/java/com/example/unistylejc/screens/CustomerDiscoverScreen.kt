package com.example.unistylejc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.unistylejc.ui.theme.Purple80

@Composable
fun CustomerDiscoverScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Center),
            verticalArrangement = Arrangement.Center){
            Text(text = "descubre", fontSize = 30.sp, color = Purple80)
        }
    }
}
