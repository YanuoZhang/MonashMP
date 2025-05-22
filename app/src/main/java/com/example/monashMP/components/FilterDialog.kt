package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class FilterData(
    val minPrice: String,
    val maxPrice: String,
    val selectedLocations: List<String>,
    val selectedCondition: String?,
    val sortBy: String
)

@Composable
fun FilterDialog(
    filterData: FilterData,
    onMinPriceChange: (String) -> Unit,
    onMaxPriceChange: (String) -> Unit,
    onLocationCheckedChange: (String) -> Unit,
    onConditionChange: (String) -> Unit,
    onSortByChange: (String) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Filters", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text("Price Range")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = filterData.minPrice,
                onValueChange = onMinPriceChange,
                placeholder = { Text("Min") },
                modifier = Modifier.weight(1f)
            )
            Text("-")
            OutlinedTextField(
                value = filterData.maxPrice,
                onValueChange = onMaxPriceChange,
                placeholder = { Text("Max") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text("Location")
        Column {
            listOf("Clayton", "Caulfield", "Parkville").forEach { location ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = location in filterData.selectedLocations,
                        onCheckedChange = { onLocationCheckedChange(location) }
                    )
                    Text(location)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Condition")
        Column {
            listOf("New", "Like New", "Good", "Fair").forEach { condition ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = filterData.selectedCondition == condition,
                        onClick = { onConditionChange(condition) }
                    )
                    Text(condition)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        SortByDropdown(
            selectedOption = filterData.sortBy,
            onOptionSelected = { onSortByChange }
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
                Text("Reset")
            }
            Button(onClick = onApply, modifier = Modifier.weight(1f)) {
                Text("Apply")
            }
        }
    }
}
