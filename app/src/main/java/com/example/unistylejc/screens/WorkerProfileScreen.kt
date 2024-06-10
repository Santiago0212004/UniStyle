package com.example.unistylejc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.CustomerProfileViewModel
import com.example.unistylejc.viewmodel.WorkerProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
private fun ScreenContent(navController: NavHostController,userState: Worker?) {
    val viewModel: WorkerProfileViewModel = viewModel()
    var showDialogLO by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Mi perfil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ProfileSection(userState)

        Spacer(modifier = Modifier.height(32.dp))

        OptionButton({
            navController.navigate("worker/settings")
        },text = "Configuración", iconResId = R.drawable.ic_settings)
        Spacer(modifier = Modifier.height(32.dp))
        OptionButton({
            navController.navigate("worker/information")
        },text = "Acerca de nosotros", iconResId = R.drawable.ic_about)
        Spacer(modifier = Modifier.height(32.dp))

        OptionButton({
            showDialogLO = true
        },text = "Cerrar sesión", iconResId = R.drawable.ic_logout)
        Spacer(modifier = Modifier.height(32.dp))

        if(showDialogLO){
            SignOutConfirmationDialog(
                onConfirm = {
                    viewModel.signOut()
                    navController.navigate("login")
                    showDialogLO = false
                },
                onDismiss = {
                    showDialogLO = false
                }
            )
        }
    }
}

@Composable
private fun ProfileSection(userState: Worker?) {
    Box(
        modifier = Modifier
            .size(width = 400.dp, height = 230.dp)
            .background(color = Color(0xFFD4BEEB), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter("${userState?.picture}"),
                contentDescription = "Imagen de perfil",
                modifier = Modifier
                    .size(124.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(width = 220.dp, height = 124.dp)
                    .shadow(3.dp,  shape = RoundedCornerShape(16.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                ,


                ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${userState?.name}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${userState?.email}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionButton( redirect: () -> Unit,
    text: String,
    iconResId: Int,
    textColor: Color = Color.Black,
    borderColor: Color = Color(0xFFFFA500)
) {
    Button(
        onClick = { redirect() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(16.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp, color = textColor)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_minimalist_arrow), // Use an appropriate icon for the arrow
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
    }
}
@Composable
fun WorkerProfileScreen(navController: NavHostController, viewModel: WorkerProfileViewModel = viewModel()) {
    val isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    val userState by viewModel.userState.observeAsState()

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadUser()
        }

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ScreenContent(navController,userState)
            }
        }
    }
}