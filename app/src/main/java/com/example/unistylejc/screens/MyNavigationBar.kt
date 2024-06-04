package com.example.unistylejc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unistylejc.BottonBarScreen
import com.example.unistylejc.R
import com.example.unistylejc.ui.theme.Purple40
import com.example.unistylejc.ui.theme.Purple80

@Composable
fun MyNavigationBar(navController: NavHostController){
    val navBarController = rememberNavController()
    val stars = painterResource(R.drawable.stars)
    val pinmap = painterResource(R.drawable.pinmap)
    val calendar = painterResource(R.drawable.calendar)
    val profile = painterResource(R.drawable.perfil)
    val selected = remember { mutableStateOf(stars) }
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Purple40
            ){
                IconButton(
                    onClick = {
                        selected.value = stars
                        navBarController.navigate(BottonBarScreen.CustomerDiscover.screen){
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ){
                    Image(
                        painter = stars,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        colorFilter = if(selected.value == stars){
                            ColorFilter.tint(Color.White)
                        }else{
                            ColorFilter.tint(Purple80)
                        }
                    )
                }

                IconButton(
                    onClick = {
                        selected.value = pinmap
                        navBarController.navigate(BottonBarScreen.CustomerReservation.screen){
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ){
                    Image(
                        painter = pinmap,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        colorFilter = if(selected.value == pinmap){
                            ColorFilter.tint(Color.White)
                        }else{
                            ColorFilter.tint(Purple80)
                        }
                    )
                }

                IconButton(
                    onClick = {
                        selected.value = calendar
                        navBarController.navigate(BottonBarScreen.ReservationCalendar.screen){
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ){
                    Image(
                        painter = calendar,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        colorFilter = if(selected.value == calendar){
                            ColorFilter.tint(Color.White)
                        }else{
                            ColorFilter.tint(Purple80)
                        }
                    )
                }

                IconButton(
                    onClick = {
                        selected.value = profile
                        navBarController.navigate(BottonBarScreen.CustomerProfile.screen){
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ){
                    Image(
                        painter = profile,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        colorFilter = if(selected.value == profile){
                            ColorFilter.tint(Color.White)
                        }else{
                            ColorFilter.tint(Purple80)
                        }
                    )
                }
            }
        }
    ) {paddingValues ->
        NavHost(navController = navBarController,
            startDestination = BottonBarScreen.CustomerReservation.screen,
            modifier = Modifier.padding(paddingValues)){
            composable(BottonBarScreen.CustomerDiscover.screen){ CustomerDiscoverScreen() }
            composable(BottonBarScreen.CustomerReservation.screen){ MainCustomerScreen(navController)}
            composable(BottonBarScreen.ReservationCalendar.screen){ CustomerReservationCalendarScreen() }
            composable(BottonBarScreen.CustomerProfile.screen){ CustomerProfileScreen() }
        }
    }


}