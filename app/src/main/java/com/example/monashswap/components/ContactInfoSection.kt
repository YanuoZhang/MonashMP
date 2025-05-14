package com.example.monashswap.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactInfoSection() {
    var email by remember { mutableStateOf("student@monash.edu") }
    var phone by remember { mutableStateOf("") }
    var contactMethod by remember { mutableStateOf("email") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text("Phone (optional)") },
        modifier = Modifier.fillMaxWidth()
    )

    Text("Preferred Contact Method", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf("email", "phone", "both").forEach { method ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = contactMethod == method, onClick = { contactMethod = method })
                Text(method.capitalize())
            }
        }
    }
}
