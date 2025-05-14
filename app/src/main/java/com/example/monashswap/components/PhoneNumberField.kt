package com.example.monashswap.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PhoneNumberField(
    labelContent: (@Composable () -> Unit)? = null
) {
    var phone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column {
        labelContent?.let {
            it()
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                error = it.isNotBlank() && !it.matches(Regex("^04\\d{8}\$"))
            },
            placeholder = { Text("Enter your phone number") },
            isError = error,
            supportingText = {
                if (error) Text("Invalid Australian mobile number", color = Color.Red)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error) Color.Red else Color(0xFF0056D2),
                unfocusedBorderColor = if (error) Color.Red else Color.LightGray
            )
        )
    }
}
