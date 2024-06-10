package com.example.unistylejc

import CustomerDiscoverScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unistylejc.screens.CustomerChangePasswordScreen
import com.example.unistylejc.screens.customerEstablishment.CustomerEstablishmentScreen
import com.example.unistylejc.screens.CustomerProfileScreen
import com.example.unistylejc.screens.CustomerSettingsScreen
import com.example.unistylejc.screens.CustomerUpdateProfileScreen
import com.example.unistylejc.screens.InformationScreen
import com.example.unistylejc.screens.LoginScreen
import com.example.unistylejc.screens.SignUpScreen
import com.example.unistylejc.screens.UploadPictureScreen
import com.example.unistylejc.screens.WorkerChangePasswordScreen
import com.example.unistylejc.screens.WorkerProfileScreen
import com.example.unistylejc.screens.WorkerReservationsScreen
import com.example.unistylejc.screens.WorkerSettingsScreen
import com.example.unistylejc.screens.WorkerUpdateProfileScreen
import com.example.unistylejc.screens.WorkerCommunityScreen
import com.example.unistylejc.screens.customerEstablishment.ReservationScreen
import com.example.unistylejc.ui.theme.UniStyleJCTheme
import com.example.unistylejc.screens.CustomerNavBar
import com.example.unistylejc.screens.CustomerReservationCalendarScreen
import com.example.unistylejc.screens.MainCustomerScreen
import com.example.unistylejc.screens.WorkerNavBar
import com.example.unistylejc.screens.WorkerServicesScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniStyleJCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                    ){
                    MainScreen()
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute !in listOf("login", "signup")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                when {
                    currentRoute?.startsWith("customer") == true -> CustomerNavBar(navController)
                    currentRoute?.startsWith("establishmentDetail/{establishmentId}") == true -> CustomerNavBar(navController)
                    currentRoute?.startsWith("reserve/{establishmentId}/{workerId}") == true -> CustomerNavBar(navController)
                    currentRoute?.startsWith("worker") == true -> WorkerNavBar(navController)
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "login", modifier = Modifier.padding(innerPadding)) {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("customer/reserva") { MainCustomerScreen(navController) }
            composable("customer/agenda") { CustomerReservationCalendarScreen(navController) }
            composable("customer/profile") { CustomerProfileScreen(navController) }
            composable("customer/discover") { CustomerDiscoverScreen(navController) }
            composable("customer/settings") { CustomerSettingsScreen(navController) }
            composable("customer/information") { InformationScreen(navController) }
            composable("customer/updateProfile") { CustomerUpdateProfileScreen(navController) }
            composable("customer/changePassword") { CustomerChangePasswordScreen(navController) }
            composable("worker/information") { InformationScreen(navController) }
            composable("worker/profile") { WorkerProfileScreen(navController) }
            composable("worker/settings") { WorkerSettingsScreen(navController) }
            composable("worker/updateProfile") { WorkerUpdateProfileScreen(navController) }
            composable("worker/changePassword") { WorkerChangePasswordScreen(navController) }
            composable("worker/reservations") { WorkerReservationsScreen(navController) }
            composable("worker/community") { WorkerCommunityScreen(navController) }
            composable("worker/services") { WorkerServicesScreen(navController) }
            composable("uploadPicture") { UploadPictureScreen(navController) }
            composable("establishmentDetail/{establishmentId}") { backStackEntry ->
                val establishmentId = backStackEntry.arguments?.getString("establishmentId")
                establishmentId?.let {
                    CustomerEstablishmentScreen(navController, it)
                }
            }
            composable("reserve/{establishmentId}/{workerId}") { backStackEntry ->
                val establishmentId = backStackEntry.arguments?.getString("establishmentId")
                val workerId = backStackEntry.arguments?.getString("workerId")
                establishmentId?.let { e ->
                    workerId?.let { w ->
                        ReservationScreen(navController, establishmentId = e, workerId = w)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UniStyleJCTheme {
        MainScreen()
    }
}