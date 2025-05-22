package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.data.model.ProductModel

@Composable
fun PostContactInfoSection(
    formState: ProductModel,
    onFieldChange: (String, String) -> Unit,
    errors: Map<String, String>
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Contact Information", fontSize = 18.sp)

        // Email - Not Editable
        Column {
            Label("Email")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.email,
                onValueChange = {},
                enabled = false,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray
                )
            )
            Text("Your university email will be used", fontSize = 12.sp, color = Color.Gray)
        }

        // Phone
        Column {
            Label("Phone Number")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.phoneNum,
                onValueChange = {
                    onFieldChange("phoneNum", it)
                },
                placeholder = { Text("Enter your phone number") },
                isError = errors["phone"] != null,
                supportingText = {
                    errors["phone"]?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errors["phone"] != null) Color.Red else Color(0xFF0056D2),
                    unfocusedBorderColor = if (errors["phone"] != null) Color.Red else Color.LightGray
                )
            )
        }

        // Preferred Contact Method
        ContactMethodPreference(
            selected = formState.preferredContactMethod,
            onSelected = { onFieldChange("preferredContactMethod", it) }
        )
    }
}