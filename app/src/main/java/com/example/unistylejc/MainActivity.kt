package com.example.unistylejc


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unistylejc.screens.CustomerDiscoverScreen
import com.example.unistylejc.screens.CustomerProfileScreen
import com.example.unistylejc.screens.CustomerReservationCalendarScreen
import com.example.unistylejc.screens.LoginScreen
import com.example.unistylejc.screens.MainCustomerScreen
import com.example.unistylejc.screens.MainWorkerScreen
import com.example.unistylejc.screens.SignUpScreen
import com.example.unistylejc.screens.UploadPictureScreen
import com.example.unistylejc.screens.MyNavigationBar
import com.example.unistylejc.ui.theme.Purple40
import com.example.unistylejc.ui.theme.UniStyleJCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniStyleJCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController = rememberNavController()){
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController)}
        composable("customer/main") { MyNavigationBar() }
        //composable("customer/main") { MainCustomerScreen(navController) }
        composable("worker/main") { MainWorkerScreen(navController) }
        composable("uploadPicture") { UploadPictureScreen(navController) }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UniStyleJCTheme {
        App()
    }
}