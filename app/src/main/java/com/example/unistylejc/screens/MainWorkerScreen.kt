package com.example.unistylejc.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.unistylejc.viewmodel.LogInViewmodel

@Composable
fun MainWorkerScreen (navController: NavHostController, loginViewModel: LogInViewmodel = viewModel()) {
    Text(text = "Soy worker", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 16.dp))
}