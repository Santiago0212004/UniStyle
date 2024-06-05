import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unistylejc.ui.theme.UniStyleJCTheme
import com.example.unistylejc.R

@Preview
@Composable
fun CustomerDiscoverScreen() {
    UniStyleJCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    HeaderSection(name = "Zara")
                }
                item {
                    SearchBar()
                }
                item {
                    DiscountCard()
                }
                item {
                    SectionTitle(title = "Nuevo para ti")
                }
                item {
                    HorizontalScrollableSection()
                }
                item {
                    SectionTitle(title = "Tus últimas visitas")
                }
                item {
                    HorizontalScrollableSection()
                }
            }
        }
    }
}

@Composable
fun HeaderSection(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "¡Hola, $name!",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D16A6)
            )
            Text(
                text = "¿Qué nos vamos a hacer hoy?",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_notification_unread),
            tint = Color(0xFF5D16A6),
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
                .padding(start = 8.dp)
        )
    }
}


@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        placeholder = { Text(text = "Encuentra lo que buscas...") },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                modifier = Modifier
                    .size(32.dp),
                contentDescription = null,
            )
        },
        shape = MaterialTheme.shapes.small
    )
}

@Composable
fun DiscountCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(134.dp)
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Text(
                    text = "15% de descuento en nuestras mejores peluquerías",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF18701)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    ) {
                    Text(text = "Conócelas")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFF18701),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun HorizontalScrollableSection() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(3) { index ->
            RecommendationCard(index)
        }
    }
}

@Composable
fun RecommendationCard(index: Int) {
    Card(
        modifier = Modifier
            .size(width = 200.dp, height = 200.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_about),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "Castaños Peluquería",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecentVisitCard(index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_delete_user),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(8.dp)
            ) {
                Text(
                    text = if (index == 0) "LA FAMA" else "NovaStyle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
