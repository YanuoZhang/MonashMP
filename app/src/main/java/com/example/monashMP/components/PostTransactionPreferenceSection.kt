package com.example.monashMP.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.data.model.ProductModel

@Composable
fun PostTransactionPreferenceSection(
    formState: ProductModel,
    meetupOptions: List<String>,
    onFieldChange: (String, String) -> Unit,
    errors: Map<String, String>
) {
    val paymentOptions = listOf("Cash", "Bank Transfer")
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Transaction Preferences", fontSize = 18.sp)

        // Meetup Point
        GenericDropdownField(
            labelContent = { Label("Preferred Meetup Spot") },
            options = meetupOptions,
            selectedOption = formState.meetupPoint,
            onOptionSelected = { onFieldChange("meetupPoint", it) },
            optionTextProvider = { it },
            placeholderText = "Select a spot near your campus",
            isError = errors["meetupPoint"] != null,
            errorMessage = errors["meetupPoint"]
        )

        // Day Preference
        Column {
            Label("Day Preference")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = formState.dayPreferenceWeekdays,
                        onCheckedChange = { onFieldChange("dayPreferenceWeekdays", it.toString()) }
                    )
                    Text("Weekdays")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = formState.dayPreferenceWeekends,
                        onCheckedChange = { onFieldChange("dayPreferenceWeekends", it.toString()) }
                    )
                    Text("Weekends")
                }
            }
        }

        // Payment Method Preference
        PaymentMethodPreference(
            options = paymentOptions,
            selectedOption = formState.paymentMethodPreference,
            onOptionSelected = { onFieldChange("paymentMethodPreference", it) }
        )

        // Notes
        Column {
            Text("Additional Notes", fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.additionalNotes,
                onValueChange = { onFieldChange("additionalNotes", it) },
                placeholder = { Text("Any special instructions for meetup") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }
    }
}
