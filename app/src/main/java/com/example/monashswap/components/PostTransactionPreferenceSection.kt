package com.example.monashswap.components


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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PostTransactionPreferenceSection() {
    val meetupPoint = listOf("Monash Library", "Woodside", "LTB")
    var selectedMeetupPoint by remember { mutableStateOf<String?>(null) }
    var weekdaysChecked by remember { mutableStateOf(false) }
    var weekendsChecked by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    val paymentOptions = listOf("Cash", "Bank Transfer" )
    var selectedPayment by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Transaction Preferences", fontSize = 18.sp, fontWeight = FontWeight.Medium)

        // Preferred Meetup Spot
        GenericDropdownField(
            labelContent = {
                Text(
                    "Preferred Meetup Spot",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            options = meetupPoint,
            selectedOption = selectedMeetupPoint,
            onOptionSelected = { selectedMeetupPoint = it },
            optionTextProvider = { it }  // 直接显示字符串本身
        )
//
        // Day Preference
        Column {
            Label("Day Preference")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = weekdaysChecked,
                        onCheckedChange = { weekdaysChecked = it }
                    )
                    Text("Weekdays")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = weekendsChecked,
                        onCheckedChange = { weekendsChecked = it }
                    )
                    Text("Weekends")
                }
            }
        }

        // Payment Method Preference
        PaymentMethodPreference(
            options = paymentOptions,
            selectedOption = selectedPayment,
            onOptionSelected = { selectedPayment = it }
        )

        // Notes
        Column {
            Text("Additional Notes", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
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
