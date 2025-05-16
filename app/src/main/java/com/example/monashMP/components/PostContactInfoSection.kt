package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.viewmodel.PostViewModel

@Composable
fun PostContactInfoSection(viewModel: PostViewModel) {
    val formState by viewModel.formState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Contact Information", fontSize = 18.sp)

        // Email
        Column {
            Label("Email")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.email,
                onValueChange = { viewModel.updateEmail(it) },
                isError = fieldErrors["email"] != null,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            fieldErrors["email"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
            Text("Your university email will be used", fontSize = 12.sp, color = Color.Gray)
        }

        // Phone
        PhoneNumberField(viewModel, labelContent = { Label("Phone Number (Optional)") })

        // Contact Method
        ContactMethodPreference(viewModel)
    }
}
