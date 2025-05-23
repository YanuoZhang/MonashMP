
package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.monashMP.data.model.ProductModel
import com.example.monashMP.utils.Constants

@Composable
fun ItemDetailSection(
    formState: ProductModel,
    onFieldChange: (String, String) -> Unit,
    errors: Map<String, String>
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Title
        Column(modifier = Modifier.fillMaxWidth()) {
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
        Column(modifier = Modifier.fillMaxWidth()) {
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
        var priceText by remember {
            mutableStateOf(
                if (formState.price == 0f) "" else formState.price.toString()
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            RequiredLabel("Price")
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        priceText = newValue
                        onFieldChange("price", newValue)
                    }
                },
                placeholder = { Text("0.00") },
                leadingIcon = { Text("$") },
                isError = errors["price"] != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errors["price"] != null) Color.Red else Color(
                        0xFF0056D2
                    ),
                    unfocusedBorderColor = if (errors["price"] != null) Color.Red else Color.LightGray
                )
            )

            errors["price"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    // Category
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Category
        GenericDropdownField(
            labelContent = { RequiredLabel("Category") },
            options = Constants.CATEGORIES,
            selectedOption = formState.category,
            onOptionSelected = { onFieldChange("category", it) },
            optionTextProvider = { it },
            placeholderText = "Select item Category",
            isError = errors["category"] != null,
            errorMessage = errors["category"]
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Condition
        GenericDropdownField(
            labelContent = { RequiredLabel("Condition") },
            options = Constants.CONDITIONS,
            selectedOption = formState.condition,
            onOptionSelected = { onFieldChange("condition", it) },
            optionTextProvider = { it },
            placeholderText = "Select item Condition",
            isError = errors["condition"] != null,
            errorMessage = errors["condition"]
        )

        Spacer(modifier = Modifier.height(24.dp))
        // Location
        GenericDropdownField(
            labelContent = { RequiredLabel("Location") },
            options = Constants.LOCATIONS,
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
