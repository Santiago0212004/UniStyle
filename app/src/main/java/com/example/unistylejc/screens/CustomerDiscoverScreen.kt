import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.unistylejc.ui.theme.UniStyleJCTheme
import com.example.unistylejc.R
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.viewmodel.CustomerDiscoverViewmodel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun CustomerDiscoverScreen(navController: NavHostController, viewModel: CustomerDiscoverViewmodel = viewModel()) {
    val reservedEstablishments by viewModel.reservedEstablishments.observeAsState()
    val unreservedEstablishments by viewModel.unreservedEstablishments.observeAsState()
    val isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    val userState by viewModel.userState.observeAsState()

    if (isAuthenticated) {
        LaunchedEffect(true) {
            viewModel.loadUser()

        }
    }
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.loadEstablishments(currentUser.uid)
        }
    }


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
                    HeaderSection(name = "${userState?.name}")
                }
                item {
                    SearchBar()
                }
                item {
                    DiscountCard(navController)
                }
                item {
                    SectionTitle(title = "Nuevo para ti")
                }
                item {
                    HorizontalScrollableSection(navController,unreservedEstablishments)
                }
                item {
                    SectionTitle(title = "Tus últimas visitas")
                }
                item {
                    HorizontalScrollableSection(navController,reservedEstablishments)
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
            .padding(vertical = 8.dp),
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
fun DiscountCard(navController: NavHostController) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }

    val gradient = Brush.horizontalGradient(
        colors = listOf(Color.Transparent, Color(0xFF6716A6)),
        endX = sizeImage.width.toFloat()/3,  // 1/3
        startX= sizeImage.width.toFloat()
    )
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
                painter = painterResource(id = R.drawable.example_offer),
                contentDescription = "", modifier = Modifier.fillMaxSize().onGloballyPositioned {
                    sizeImage = it.size
                })
            Box(modifier = Modifier.matchParentSize().background(gradient))
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
                    onClick = { navController.navigate("customer/main") },
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
fun HorizontalScrollableSection(
    navController: NavHostController,
    establishments: List<Establishment?>?
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (establishments != null) {
            if (establishments.isEmpty()) {
            } else {
                items(establishments) { establishment ->
                RecommendationCard(navController ,establishment)
              }
            }
        }
    }
}

@Composable
fun RecommendationCard(navController: NavHostController,establishment: Establishment?) {
    Card(
        modifier = Modifier
            .size(width = 200.dp, height = 200.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter("${establishment?.picture}"),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
                    .clickable { navController.navigate("establishmentDetail/${establishment?.id}") },
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "${establishment?.name}",
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
