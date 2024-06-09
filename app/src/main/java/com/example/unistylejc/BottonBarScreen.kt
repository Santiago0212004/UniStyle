package com.example.unistylejc

sealed class BottonBarScreen (val screen: String){
    data object CustomerDiscover: BottonBarScreen("CustomerDiscoverScreen")
    data object CustomerReservation: BottonBarScreen( "MainCustomerScreen")
    data object ReservationCalendar: BottonBarScreen("CustomerReservationCalendarScreen")
    data object CustomerProfile: BottonBarScreen("CustomerProfileScreen")
    data object SettingsCustomer: BottonBarScreen("customer/settings")
    data object MainWorkerScreen: BottonBarScreen("MainWorkerScreen")
    data object WorkerProfileScreen: BottonBarScreen("WorkerProfileScreen")
    data object WorkerReservationsScreen: BottonBarScreen("WorkerReservationsScreen")
    data object WorkerSettingsScreen: BottonBarScreen("worker/settings")
}