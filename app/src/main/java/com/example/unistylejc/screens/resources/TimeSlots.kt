package com.example.unistylejc.screens.resources

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.unistylejc.viewmodel.CustomerEstablishmentViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlots(
    viewModel: CustomerEstablishmentViewModel,
    date: LocalDate,
    selectedTimeRange: Pair<LocalTime, LocalTime>?,
    onTimeRangeSelected: (Pair<LocalTime, LocalTime>) -> Unit
) {
    val reservations by viewModel.selectedWorkerReservations.observeAsState(emptyList())

    val startHour = LocalTime.of(8, 0)
    val endHour = LocalTime.of(22, 0)
    val timeSlots = generateTimeSlots(startHour, endHour)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        timeSlots.forEach { slot ->
            val isAvailable = reservations.none { res ->
                val resDate = res.initDate?.toDate()?.toInstant()?.atZone(ZoneOffset.ofHours(-5))?.toLocalDate()
                val resStart = res.initDate?.toDate()?.toInstant()?.atZone(ZoneOffset.ofHours(-5))?.toLocalTime()
                val resEnd = res.finishDate?.toDate()?.toInstant()?.atZone(ZoneOffset.ofHours(-5))?.toLocalTime()
                (resDate == date && resStart != null && resEnd != null && slot.first.isBefore(resEnd) && slot.second.isAfter(resStart))
            }

            Column {
                TimeSlotItem(
                    slot = slot,
                    isAvailable = isAvailable,
                    isSelected = selectedTimeRange == slot,
                    onClick = { onTimeRangeSelected(slot) }
                )
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateTimeSlots(start: LocalTime, end: LocalTime): List<Pair<LocalTime, LocalTime>> {
    val slots = mutableListOf<Pair<LocalTime, LocalTime>>()
    var currentStart = start
    while (currentStart.isBefore(end)) {
        val currentEnd = currentStart.plusMinutes(29)
        if (currentEnd.isAfter(end)) break
        slots.add(currentStart to currentEnd)
        currentStart = currentEnd.plusMinutes(1)
    }
    return slots
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotItem(
    slot: Pair<LocalTime, LocalTime>,
    isAvailable: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isAvailable -> MaterialTheme.colorScheme.surface
        else -> Color.Red
    }

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(color)
            .clickable(enabled = isAvailable, onClick = onClick)
    ) {
        Text(
            text = "${slot.first.format(timeFormatter)} - ${slot.second.format(timeFormatter)}",
            modifier = Modifier.padding(8.dp),
            color = if (isAvailable) MaterialTheme.colorScheme.onSurface else Color.DarkGray
        )
    }
}
