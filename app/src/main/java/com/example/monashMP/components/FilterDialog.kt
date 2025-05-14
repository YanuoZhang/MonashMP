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

//@Composable
//fun FilterDialog(
//    onDismiss: () -> Unit
//) {
//    var minPrice by remember { mutableStateOf("") }
//    var maxPrice by remember { mutableStateOf("") }
//    var expanded by remember { mutableStateOf(false) }
//    val sortOptions = listOf("Newest First", "Price: Low to High", "Price: High to Low")
//    var selectedOption by remember { mutableStateOf(sortOptions[0]) }
//    Dialog(
//        onDismissRequest = onDismiss
//    ) {
//        Surface(
//            shape = RoundedCornerShape(24.dp),
//            color = Color.White,
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(12.dp)
//                    .verticalScroll(rememberScrollState())
//                    .fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Filters", style = MaterialTheme.typography.titleLarge)
//                    IconButton(onClick = onDismiss) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//                }
//                Text("Price Range", style = MaterialTheme.typography.bodyLarge)
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    OutlinedTextField(
//                        value = minPrice,
//                        onValueChange = { minPrice = it },
//                        placeholder = {
//                            Text(
//                                text = "Min",
//                                fontSize = 14.sp,
//                                color = Color.Gray
//                            )
//                        },
//                        textStyle = TextStyle(fontSize = 12.sp),
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(50.dp)
//                            .padding(0.dp),
//                        shape = RoundedCornerShape(24.dp),
//                        singleLine = true,
//                    )
//
//
//                    Text("-") // 分割符号
//                    OutlinedTextField(
//                        value = maxPrice,
//                        onValueChange = { maxPrice = it  },
//                        placeholder = { Text(
//                            "Max",
//                            fontSize = 14.sp,
//                            color = Color.Gray
//                        )
//                        },
//                        textStyle = TextStyle(fontSize = 12.sp),
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(50.dp)
//                            .padding(0.dp),
//                        shape = RoundedCornerShape(24.dp),
//                        singleLine = true,
//                    )
//                }
//
//                Text("Location")
//                Column( verticalArrangement = Arrangement.spacedBy(0.dp))
//                {
//                    listOf("Clayton", "Caulfield", "Parkville").forEach { location ->
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Checkbox(
//                                checked = false,
//                                onCheckedChange = {}
//                            )
//                            Text(location, fontSize = 14.sp)
//                        }
//                    }
//                }
//
//                Text("Condition")
//                Column {
//                    listOf("New", "Like New", "Good", "Fair").forEach { condition ->
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            RadioButton(
//                                selected = condition == "Good",
//                                onClick = { /* TODO */ }
//                            )
//                            Text(condition, fontSize = 14.sp)
//                        }
//                    }
//                }
//
//                Text("Sort By")
//                Box {
//                    OutlinedTextField(
//                        value = selectedOption,
//                        onValueChange = {},
//                        readOnly = true,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { expanded = true },  // 整个 TextField 可点击
//                        trailingIcon = {
//                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                        },
//                        shape = RoundedCornerShape(24.dp)
//                    )
//
//                    DropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false }
//                    ) {
//                        sortOptions.forEach { option ->
//                            DropdownMenuItem(
//                                text = { Text(option) },
//                                onClick = {
//                                    selectedOption = option
//                                    expanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//
//                Button(
//                    onClick = onDismiss,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Apply")
//                }
//            }
//        }
//    }
//}