package com.example.monashMP.components

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.monashMP.viewmodel.PostViewModel

@Composable
fun PhoneNumberField(
    viewModel: PostViewModel,
    labelContent: (@Composable () -> Unit)? = null
) {
    val formState by viewModel.formState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()

    Column {
        labelContent?.let {
            it()
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = formState.phoneNum,
            onValueChange = {
                viewModel.updatePhoneNum(it)
            },
            placeholder = { Text("Enter your phone number") },
            isError = fieldErrors["phone"] != null,
            supportingText = {
                fieldErrors["phone"]?.let {
                    Text(it, color = Color.Red)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (fieldErrors["phone"] != null) Color.Red else Color(0xFF0056D2),
                unfocusedBorderColor = if (fieldErrors["phone"] != null) Color.Red else Color.LightGray
            )
        )
    }
}
