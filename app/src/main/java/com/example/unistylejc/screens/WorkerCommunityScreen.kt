package com.example.unistylejc.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.CommentEntity
import com.example.unistylejc.domain.model.ResponseEntity
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.screens.resources.RatingStars
import com.example.unistylejc.viewmodel.WorkerCommunityViewmodel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun WorkerCommunityScreen(navHostController: NavHostController, viewModel: WorkerCommunityViewmodel= viewModel()) {
    val isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    val userState by viewModel.userState.observeAsState()
    val comments by viewModel.commentListState.observeAsState()

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadUser()
            viewModel.observeUser();
        }
    }
    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadComments()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileHeader(userState)
        Spacer(modifier = Modifier.height(16.dp))
        CommentsSection(viewModel,comments,userState,navHostController)
    }
}

@Composable
fun ProfileHeader(userState: Worker?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color(0xFFF2E8FC), shape = RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter("${userState?.picture}"),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${userState?.name}",
                    fontSize = 24.sp, // Adjust the font size to match the image
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A1285)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    userState?.score?.let {
                        RatingStars(it)
                    }
                }
            }
        }
    }
}


@Composable
fun CommentsSection(viewModel: WorkerCommunityViewmodel,comments: List<CommentEntity>?, userState: Worker?,navHostController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    LazyColumn {
        item(){
            Spacer(modifier = Modifier.height(16.dp))
            comments?.forEach() {
                CommentCard(viewModel,it,userState,expanded)
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentCard(viewModel: WorkerCommunityViewmodel,comment: CommentEntity,userState: Worker?,expanded: Boolean) {
    var showReplies by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }
    var showTextField by remember { mutableStateOf(true) }

    if (showDialog) {
        MinimalDialog(onDismissRequest = { showDialog = false })
        LaunchedEffect(Unit) {
            delay(3000L) // Delay for 2 seconds
            showDialog = false
            shouldNavigate = true
            viewModel.loadComments()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFD4BEEB), shape = RoundedCornerShape(16.dp),)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter("${comment.customer?.picture}"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "${comment.customer?.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp,color = Color(0xFF5D16A6) )

                    Spacer(modifier = Modifier.height(8.dp))

                    comment.date?.toDate()?.let {

                        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

                        val date = comment.date?.toDate()?.let { d -> sdf.format(d) } ?: "Unknown date"

                        Text(text = date, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    RatingStars(comment.score)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.content,
                fontWeight = FontWeight.Bold,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )
            comment.responseEntity?.let { response ->
                if (response.content!=""){
                    if(!showReplies){
                        TextButton(onClick = { showReplies = !showReplies }) {
                            Text("Respuesta", color = Color.Black, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
                        }
                    }
                    if (showReplies) {
                        TextButton(onClick = { showReplies = !showReplies }) {
                            Text("Respuesta",color = Color.Black, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Contract")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        if (userState != null) {
                            ReplyCard(userState, response)
                        }
                    }
                }else{
                    if (showTextField){
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("Responder...") },
                            modifier = Modifier
                                .fillMaxWidth() // Set the desired width
                                .height(56.dp), // Set the desired height
                            shape = RoundedCornerShape(18.dp), // Set the rounded corners
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                unfocusedLabelColor = Color.Black,
                                unfocusedPlaceholderColor = Color.Black,
                                unfocusedLeadingIconColor = Color.Black,
                                focusedTextColor = Color.Black,
                                focusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    tint = Color.Black,
                                    contentDescription = "Send",
                                    modifier = Modifier.clickable {
                                        showTextField=false
                                        userState?.id?.let {
                                            viewModel.sendResponse(comment.id, it,text)
                                        }
                                        showDialog=true
                                    }
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyCard(worker:Worker,reply: ResponseEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFD4BEEB),)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(){
                Text(text = worker.name, fontSize = 14.sp, color = Color(0xFF5D16A6), fontWeight = FontWeight.Bold)
            }
            Text(text = reply.content, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
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
                    text = "Se publicó el comentario",
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