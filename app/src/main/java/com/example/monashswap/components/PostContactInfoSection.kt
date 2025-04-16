package com.example.monashswap.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PostContactInfoSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Contact Information", fontSize = 18.sp, fontWeight = FontWeight.Medium)

        // Email
        Column {
            Label("Email")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = "student@monash.edu",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Gray,
                    disabledBorderColor = Color.LightGray
                )
            )
            Text("Your university email will be used", fontSize = 12.sp, color = Color.Gray)
        }

        // Phone
        PhoneNumberField(
            labelContent = { Label("Phone Number (Optional)") }
        )

        // Contact Method
        ContactMethodPreference()
    }
}
