package com.example.monashswap.components

import android.app.DatePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun BirthdatePickerField(
    label: String = "Date of Birth",
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    hintText: String = "This will not be shared with anyone"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val interactionSource = remember { MutableInteractionSource() }
    Column {
        Label(label)
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDatePickerDialog(context, year, month, day, onDateSelected)
            }
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Select your birthdate") },
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.LightGray,
                    disabledTextColor = Color.Black,
                    disabledTrailingIconColor = Color.Gray,
                    disabledPlaceholderColor = Color.Gray
                )
            )
        }

        Text(
            hintText,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun showDatePickerDialog(
    context: Context,
    year: Int,
    month: Int,
    day: Int,
    onDateSelected: (String) -> Unit
) {
    val themedContext = ContextThemeWrapper(context, android.R.style.Theme_Material)
    DatePickerDialog(
        themedContext,
        { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(formattedDate)
        },
        year,
        month,
        day
    ).show()
}
