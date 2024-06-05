package com.example.unistylejc.screens.resources

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    title: String = "Select Date",
    onDismissRequest: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberDatePickerState(): DatePickerState {
    val currentDate = LocalDate.now()
    return remember { DatePickerState(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth) }
}

data class DatePickerState(
    var year: Int,
    var month: Int,
    var day: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(state: DatePickerState, onDismissRequest: () -> Unit, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                state.year = year
                state.month = month + 1
                state.day = dayOfMonth
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            state.year,
            state.month - 1,
            state.day
        )

        datePickerDialog.setOnDismissListener {
            onDismissRequest()
        }

        datePickerDialog.show()

        onDispose {
            datePickerDialog.dismiss()
        }
    }
}
