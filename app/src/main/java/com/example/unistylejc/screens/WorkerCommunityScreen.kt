import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.unistylejc.R
@Preview

@Composable
fun WorkerCommunityScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileHeader()
        Spacer(modifier = Modifier.height(16.dp))
        CommentsSection()
    }
}

@Composable
fun ProfileHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error_circle),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(50))
        )
        Text(text = "Majo Vargas", fontSize = 24.sp)
        Text(text = "Peluquería", fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) {
                Icon(painter = painterResource(id = R.drawable.ic_error_circle), contentDescription = "Star", tint = Color.Yellow)
            }
        }
    }
}

@Composable
fun CommentsSection() {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { /* Handle Reservations */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
            Text("Reservas")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* Handle Comments */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)) {
            Text("Comentarios", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        repeat(3) {
            CommentCard(expanded) { expanded = !expanded }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CommentCard(expanded: Boolean, onExpand: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter("https://example.com/user_profile.jpg"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Santiago Barraza")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )
            TextButton(onClick = onExpand) {
                Text("Respuestas")
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
            }
        }
    }
}