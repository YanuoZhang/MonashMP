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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.utils.Const

@Composable
fun ItemDetailSection(
    formState: ProductEntity,
    onFieldChange: (String, String) -> Unit,
    errors: Map<String, String>
) {
    val categories = listOf(Const.ELECTRONICS, Const.HOME, Const.CLOTHING, Const.BOOKS, Const.OTHERS)
    val locations = listOf("Clayton", "Caulfield")
    val conditions = listOf("Brand New", "Like New", "Used", "Heavily Used")

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title
        Column {
            RequiredLabel("Title")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.title,
                onValueChange = { onFieldChange("title", it) },
                isError = errors["title"] != null,
                placeholder = { Text("What are you selling?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            errors["title"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Description
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RequiredLabel("Description")
                Text("${formState.desc.length}/500", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.desc,
                onValueChange = {
                    if (it.length <= 500) onFieldChange("desc", it)
                },
                isError = errors["desc"] != null,
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
            errors["desc"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Price
        Column {
            RequiredLabel("Price")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = if (formState.price == 0f && formState.price.toString() != "0.0") "" else formState.price.toString(),
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        onFieldChange("price", newValue)
                    }
                },
                isError = errors["price"] != null,
                placeholder = { Text("0.00") },
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errors["price"] != null) Color.Red else Color(0xFF0056D2),
                    unfocusedBorderColor = if (errors["price"] != null) Color.Red else Color.LightGray
                )
            )
            errors["price"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Category
        GenericDropdownField(
            labelContent = { RequiredLabel("Category") },
            options = categories,
            selectedOption = formState.category,
            onOptionSelected = { onFieldChange("category", it) },
            optionTextProvider = { it },
            placeholderText = "Select item Category",
            isError = errors["category"] != null,
            errorMessage = errors["category"]
        )

        // Condition
        GenericDropdownField(
            labelContent = { RequiredLabel("Condition") },
            options = conditions,
            selectedOption = formState.condition,
            onOptionSelected = { onFieldChange("condition", it) },
            optionTextProvider = { it },
            placeholderText = "Select item Condition",
            isError = errors["condition"] != null,
            errorMessage = errors["condition"]
        )

        // Location
        GenericDropdownField(
            labelContent = { RequiredLabel("Location") },
            options = locations,
            selectedOption = formState.location,
            onOptionSelected = { onFieldChange("location", it) },
            optionTextProvider = { it },
            placeholderText = "Select item Location",
            isError = errors["location"] != null,
            errorMessage = errors["location"]
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

