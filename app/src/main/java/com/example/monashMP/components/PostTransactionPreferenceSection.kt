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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.viewmodel.PostViewModel

@Composable
fun PostTransactionPreferenceSection(
    formState: com.example.monashMP.model.ProductModel,
    onUpdate: (com.example.monashMP.model.ProductModel.() -> com.example.monashMP.model.ProductModel) -> Unit,
    viewModel: PostViewModel
) {
    val meetupPoints = when (formState.location) {
        "Clayton" -> listOf("LTB", "SML Library", "Monash sport", "Monash CLUB", "Bus stop", "Learning Village")
        "Caulfield" -> listOf("Building H", "Monash sport", "Library")
        else -> emptyList()
    }
    val paymentOptions = listOf("Cash", "Bank Transfer")

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Transaction Preferences", fontSize = 18.sp, fontWeight = FontWeight.Medium)

        // Preferred Meetup Spot
        GenericDropdownField(
            labelContent = {
                Text("Preferred Meetup Spot", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            },
            options = meetupPoints,
            selectedOption = formState.meetupPoint,
            onOptionSelected = { viewModel.updateMeetupPoint(it) },
            optionTextProvider = { it }
        )

        // Day Preference
        Column {
            Label("Day Preference")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = formState.dayPreferenceWeekdays,
                        onCheckedChange = { viewModel.updateDayPreferenceWeekdays(it) }
                    )
                    Text("Weekdays")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = formState.dayPreferenceWeekends,
                        onCheckedChange = { viewModel.updateDayPreferenceWeekends(it) }
                    )
                    Text("Weekends")
                }
            }
        }

        // Payment Method Preference
        PaymentMethodPreference(
            options = paymentOptions,
            selectedOption = formState.paymentMethodPreference,
            onOptionSelected = { viewModel.updatePaymentMethodPreference(it) }
        )

        // Notes
        Column {
            Text("Additional Notes", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.additionalNotes,
                onValueChange = { viewModel.updateAdditionalNotes(it) },
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

