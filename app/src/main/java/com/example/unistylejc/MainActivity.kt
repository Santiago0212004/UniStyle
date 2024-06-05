package com.example.unistylejc


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unistylejc.screens.customerEstablishment.CustomerEstablishmentScreen
import com.example.unistylejc.screens.LoginScreen
import com.example.unistylejc.screens.MainWorkerScreen
import com.example.unistylejc.screens.MyNavigationBar
import com.example.unistylejc.screens.SignUpScreen
import com.example.unistylejc.screens.UploadPictureScreen
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(navController: NavHostController = rememberNavController()){
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController)}
        composable("customer/main") { MyNavigationBar(navController) }
        composable("worker/main") { MainWorkerScreen(navController) }
        composable("uploadPicture") { UploadPictureScreen(navController) }
        composable("establishmentDetail/{establishmentId}") { backStackEntry ->
            val establishmentId = backStackEntry.arguments?.getString("establishmentId")
            establishmentId?.let {
                CustomerEstablishmentScreen(navController, it)
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UniStyleJCTheme {
        App()
    }
}