package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.sp
import com.example.monashMP.model.Location


@Composable
fun ItemDetailSection() {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf("") }

    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }

    var amount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }

    val categories = listOf("Electronics", "Books", "Clothing")
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val locations = listOf(Location("clay", "Clayton"), Location("cau", "Caulfield"))
    var selectedLocation by remember { mutableStateOf<Location?>(null) }

    val conditions = listOf("Brand New", "Like New", "Used", "Heavily Used")
    var selectedCondition by remember { mutableStateOf<String?>(null) }
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title
        Column {
            RequiredLabel("Title")

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = if (it.isBlank()) "Title is required" else ""
                },
                isError = titleError.isNotEmpty(),
                placeholder = { Text("What are you selling?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            if (titleError.isNotEmpty()) {
                Text(text = titleError, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Description
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RequiredLabel("Description")
                Text("0/500", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 500) {
                        description = it
                        descriptionError = if (it.isBlank()) "Description is required" else ""
                    }
                },
                isError = descriptionError.isNotEmpty(),
                placeholder = { Text("Describe your item in detail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            if (descriptionError.isNotEmpty()) {
                Text(descriptionError, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Price
        Column {
            RequiredLabel("Price")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        amount = newValue
                        amountError = when {
                            newValue.isBlank() -> "Price is required"
                            newValue.toFloatOrNull() == 0f -> "Price must be greater than 0"
                            else -> ""
                        }
                    }
                },
                placeholder = { Text("0.00") },
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            if (amountError.isNotEmpty()) {
                Text(amountError, color = Color.Red, fontSize = 12.sp)
            }

        }

        // Category
        GenericDropdownField(
            labelContent = { RequiredLabel("Category") },
            options = categories,
            selectedOption = selectedCategory,
            onOptionSelected = { selectedCategory = it },
            optionTextProvider = { it },
            placeholderText = "Select item Category"
        )
        // Condition
        GenericDropdownField(
            labelContent = { RequiredLabel("Condition") },
            options = conditions,
            selectedOption = selectedCondition,
            onOptionSelected = { selectedCondition = it },
            optionTextProvider = { it },
            placeholderText = "Select item Condition"
        )

        // Location
        GenericDropdownField(
            labelContent = { RequiredLabel("Location") },
            options = locations,
            selectedOption = selectedLocation,
            onOptionSelected = { selectedLocation = it },
            optionTextProvider = { it.name }
        )
    }
}
