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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.monashMP.model.FilterData
import com.example.monashMP.utils.Constants

@Composable
fun FilterDialog(
    filterData: FilterData,
    onMinPriceChange: (Float) -> Unit,
    onMaxPriceChange: (Float) -> Unit,
    onLocationCheckedChange: (String) -> Unit,
    onConditionChange: (String) -> Unit,
    onSortByChange: (String) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    var minPriceText by remember {
        mutableStateOf(
            if (filterData.minPrice == 0f) "" else filterData.minPrice.toString()
        )
    }
    var maxPriceText by remember {
        mutableStateOf(
            if (filterData.maxPrice == Float.MAX_VALUE) "" else filterData.maxPrice.toString()
        )
    }

    LaunchedEffect(filterData.minPrice) {
        minPriceText = if (filterData.minPrice == 0f) "" else filterData.minPrice.toString()
    }
    LaunchedEffect(filterData.maxPrice) {
        maxPriceText = if (filterData.maxPrice == Float.MAX_VALUE) "" else filterData.maxPrice.toString()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Filters", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text("Price Range")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = minPriceText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        minPriceText = newValue
                        onMinPriceChange(newValue.toFloatOrNull()?:0f )
                    }
                },
                placeholder = { Text("Min") },
                modifier = Modifier.weight(1f)
            )
            Text("-")
            OutlinedTextField(
                value = maxPriceText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        maxPriceText = newValue
                        onMaxPriceChange(newValue.toFloatOrNull()?:0f )
                    }
                },
                placeholder = { Text("Max") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text("Location")
        Column {
            Constants.LOCATIONS.forEach { location ->
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
            Constants.CONDITIONS.forEach { condition ->
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
            onOptionSelected = { newOption -> onSortByChange(newOption) }
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
