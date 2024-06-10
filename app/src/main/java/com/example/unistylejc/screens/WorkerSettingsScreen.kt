package com.example.unistylejc.screens

import android.net.Uri
import android.service.autofill.OnClickAction
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.viewmodel.WorkerProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
private fun ScreenContent(navController: NavHostController,userState: Worker?,viewModel: WorkerProfileViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        MinimalDialog(onDismissRequest = { showDialog = false })
        LaunchedEffect(Unit) {
            delay(3000L) // Delay for 3 seconds
            showDialog = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navController.navigate("worker/profile") },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Mi perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        ProfileSection(navController,userState,viewModel,showDialog){
            showDialog = it;
        }

        Spacer(modifier = Modifier.height(32.dp))

        OptionButton({
            navController.navigate("worker/updateProfile")
        }, text = "Cambiar datos", iconResId = R.drawable.ic_settings)
        Spacer(modifier = Modifier.height(32.dp))
        OptionButton({navController.navigate("worker/changePassword")},
            text = "Cambiar contraseña",
            iconResId = R.drawable.ic_password
        )
        Spacer(modifier = Modifier.height(32.dp))
        OptionButton({},
            text = "Desvincular",
            iconResId = R.drawable.ic_logout
        )
        Spacer(modifier = Modifier.height(32.dp))
        OptionButton({},
            text = "Eliminar cuenta",
            iconResId = R.drawable.ic_delete_user,
            textColor = Color.Red,
            borderColor = Color.Red
        )
    }
}

@Composable
private fun ProfileSection(navController: NavHostController,userState: Worker?,viewModel: WorkerProfileViewModel,dialog:Boolean,
                           showDialogChange: (Boolean) -> Unit,) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    LaunchedEffect(imageUri.value) {
        if (imageUri.value != null) {
            imageUri.value?.let {
                viewModel.uploadProfilePicture(it, true) { success ->
                    if (success) {
                        showDialogChange(true)
                    } else {
                        Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

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
            Box(
                modifier = Modifier
                    .size(124.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter("${userState?.picture}"),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(124.dp)
                        .clip(CircleShape)
                        .clickable {pickImageLauncher.launch("image/*")},

                    contentScale = ContentScale.Crop
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_pencil),
                    contentDescription = "Editar imagen de perfil",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White, shape = CircleShape)
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(width = 220.dp, height = 124.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(16.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
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
private fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
                Text(
                    text = "Se realizaron los cambios",
                    modifier = Modifier
                        .fillMaxSize()
                        .size(36.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}
@Composable
fun WorkerSettingsScreen(navController: NavHostController, viewModel: WorkerProfileViewModel = viewModel()) {
    val isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    val userState by viewModel.userState.observeAsState()

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadUser()
            viewModel.observeUser()
        }

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ScreenContent(navController, userState,viewModel)
            }
        }
    }
}

