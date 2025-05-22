package com.example.monashMP.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    filterData: FilterData,
    onUpdateFilter: (FilterData) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onClose() },
        sheetState = sheetState
    ) {
        FilterDialog(
            filterData = filterData,
            onMinPriceChange = { onUpdateFilter(filterData.copy(minPrice = it)) },
            onMaxPriceChange = { onUpdateFilter(filterData.copy(maxPrice = it)) },
            onLocationCheckedChange = { location ->
                onUpdateFilter(
                    filterData.copy(
                        selectedLocations = if (location in filterData.selectedLocations)
                            filterData.selectedLocations - location
                        else
                            filterData.selectedLocations + location
                    )
                )
            },
            onConditionChange = { onUpdateFilter(filterData.copy(selectedCondition = it)) },
            onSortByChange = { onUpdateFilter(filterData.copy(sortBy = it)) },
            onReset = onReset,
            onApply = onApply
        )
    }
}
