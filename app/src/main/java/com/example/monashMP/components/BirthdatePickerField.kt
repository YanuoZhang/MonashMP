package com.example.monashMP.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.utils.showDatePickerDialog

@Composable
fun BirthdatePickerField(
    label: String = "Date of Birth",
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    hintText: String = "This will not be shared with anyone"
) {
    val context = LocalContext.current

    Column {
        Label(label)
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDatePickerDialog(context, onDateSelected)
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