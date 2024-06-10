package com.example.unistylejc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.unistylejc.R

@Composable
fun CustomerNavBar(navController: NavController) {
    val items = listOf(
        NavItem.Discover,
        NavItem.Reservations,
        NavItem.Agenda,
        NavItem.Profile
    )
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        containerColor = Color(0xFFF3E5F5),
        contentColor = Color.Black
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selected) Color(0xFFD4BEEB) else Color.Transparent,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            tint = if (selected) Color.Black else Color.Gray,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) Color.Black else Color.Gray
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun WorkerNavBar(navController: NavController) {
    val items = listOf(
        NavItem.WorkerCommunity,
        NavItem.WorkerReservations,
        NavItem.WorkerProfile
    )
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        containerColor = Color(0xFFF3E5F5),
        contentColor = Color.Black
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.value?.destination?.route
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selected) Color(0xFFD4BEEB) else Color.Transparent,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ){
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            tint = if (selected) Color.Black else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) Color.Black else Color.Gray
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

sealed class NavItem(val route: String, val icon: Int, val title: String) {
    object Discover : NavItem("customer/discover", R.drawable.stars, "Descubre")
    object Reservations : NavItem("customer/reserva", R.drawable.pinmap, "Reserva")
    object Agenda : NavItem("customer/agenda", R.drawable.calendar, "Agenda")
    object Profile : NavItem("customer/profile", R.drawable.perfil, "Perfil")

    object WorkerCommunity : NavItem("worker/community", R.drawable.stars,"Comunidad")
    object WorkerReservations : NavItem("worker/reservations", R.drawable.calendar, "Agenda")
    object WorkerProfile : NavItem("worker/profile", R.drawable.perfil, "Perfil")
}