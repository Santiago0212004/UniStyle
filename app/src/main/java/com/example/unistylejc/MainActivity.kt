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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.unistylejc.viewmodel.LogInViewmodel
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.unistylejc.viewmodel.SignUpViewmodel
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset

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
                    val navController = rememberNavController()
                    val loginViewModel: LogInViewmodel = viewModel()
                    val signUpViewModel: SignUpViewmodel = viewModel()

                    // Load user session
                    LaunchedEffect(Unit) {
                        loginViewModel.loadUserSession(applicationContext)
                        signUpViewModel.loadUserSession(applicationContext)
                    }
                    MainScreen(navController, loginViewModel)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController = rememberNavController(), loginViewModel: LogInViewmodel = viewModel()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in listOf("login", "signup", "splash")

    val authState by loginViewModel.authStatus.observeAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AppAuthState.SuccessLogin -> {
                when (state.role) {
                    "worker" -> navController.navigate("worker/community") {
                        popUpTo("splash") { inclusive = true }
                    }
                    "customer" -> navController.navigate("customer/discover") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            else -> Unit
        }
    }

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
        NavHost(navController, startDestination = "splash", modifier = Modifier.padding(innerPadding)) {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("splash") { SplashScreen(navController, loginViewModel) }
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

@Composable
fun SplashScreen(navController: NavHostController, loginViewModel: LogInViewmodel) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500)
        showSplash = false
    }

    if (showSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    drawCircle(
                        color = Color(0xFFFFC0CB),
                        center = Offset(x = 0.1f * canvasWidth, y = 0.1f * canvasHeight),
                        radius = 0.2f * canvasWidth
                    )

                    drawCircle(
                        color = Color(0xFFA7A7FF),
                        center = Offset(x = 0.9f * canvasWidth, y = 0.3f * canvasHeight),
                        radius = 0.15f * canvasWidth
                    )

                    drawCircle(
                        color = Color(0xFFFFE1B5),
                        center = Offset(x = 0.2f * canvasWidth, y = 0.9f * canvasHeight),
                        radius = 0.25f * canvasWidth
                    )

                    drawCircle(
                        color = Color(0xFFE4C2FF),
                        center = Offset(x = 0.8f * canvasWidth, y = 0.8f * canvasHeight),
                        radius = 0.1f * canvasWidth
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null
                )
            }
        }
    } else {
        CheckAuthState(navController, loginViewModel)
    }
}

@Composable
fun CheckAuthState(navController: NavHostController, loginViewModel: LogInViewmodel) {
    val authState by loginViewModel.authStatus.observeAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AppAuthState.SuccessLogin -> {
                when (state.role) {
                    "worker" -> navController.navigate("worker/community") {
                        popUpTo("splash") { inclusive = true }
                    }
                    "customer" -> navController.navigate("customer/discover") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            else -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
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