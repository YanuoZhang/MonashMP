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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.model.ProductModel
import com.example.monashMP.utils.Const.BOOKS
import com.example.monashMP.utils.Const.CLOTHING
import com.example.monashMP.utils.Const.ELECTRONICS
import com.example.monashMP.utils.Const.HOME
import com.example.monashMP.utils.Const.OTHERS
import com.example.monashMP.viewmodel.PostViewModel

@Composable
fun ItemDetailSection(
    formState: ProductModel,
    onUpdate: (ProductModel.() -> ProductModel) -> Unit,
    viewModel: PostViewModel
) {
    val fieldErrors by viewModel.fieldErrors.collectAsState()

    val categories = listOf(ELECTRONICS, HOME, CLOTHING, BOOKS, OTHERS)
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
                onValueChange = { onUpdate { copy(title = it) } },
                isError = fieldErrors["title"] != null,
                placeholder = { Text("What are you selling?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            fieldErrors["title"]?.let {
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
                    if (it.length <= 500) onUpdate { copy(desc = it) }
                },
                isError = fieldErrors["desc"] != null,
                placeholder = { Text("Describe your item in detail") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color.LightGray
                )
            )
            fieldErrors["desc"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Price
        Column {
            RequiredLabel("Price")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = if (formState.price == 0f) "" else formState.price.toString(),
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        onUpdate { copy(price = newValue.toFloatOrNull() ?: 0f) }
                    }
                },
                isError = fieldErrors["price"] != null,
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
            fieldErrors["price"]?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }
        }

        // Category
        GenericDropdownField(
            labelContent = { RequiredLabel("Category") },
            options = categories,
            selectedOption = formState.category,
            onOptionSelected = { onUpdate { copy(category = it) } },
            optionTextProvider = { it },
            placeholderText = "Select item Category",
            isError = fieldErrors["category"] != null,
            errorMessage = fieldErrors["category"]
        )

        // Condition
        GenericDropdownField(
            labelContent = { RequiredLabel("Condition") },
            options = conditions,
            selectedOption = formState.condition,
            onOptionSelected = { onUpdate { copy(condition = it) } },
            optionTextProvider = { it },
            placeholderText = "Select item Condition",
            isError = fieldErrors["condition"] != null,
            errorMessage = fieldErrors["condition"]
        )

        // Location
        GenericDropdownField(
            labelContent = { RequiredLabel("Location") },
            options = locations,
            selectedOption = formState.location,
            onOptionSelected = { onUpdate { copy(location = it) } },
            optionTextProvider = { it },
            placeholderText = "Select item Location",
            isError = fieldErrors["location"] != null,
            errorMessage = fieldErrors["location"]
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
