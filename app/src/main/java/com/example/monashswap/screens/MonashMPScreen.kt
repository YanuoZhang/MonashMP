package com.example.monashswap.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.monashswap.R
import com.example.monashswap.components.BottomNavBar
import com.example.monashswap.components.CategoryChips
import com.example.monashswap.components.FilterData
import com.example.monashswap.components.FilterDialog
import com.example.monashswap.components.ItemGrid
import com.example.monashswap.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonashMPScreen(navController: NavHostController) {
    var query by remember { mutableStateOf("") }
    val selectedCategory = remember { mutableStateOf("All") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilter by remember { mutableStateOf(true) }
    val pacificoFont = FontFamily(Font(R.font.lilita_one))



    var filterData by remember {
        mutableStateOf(
            FilterData(
                minPrice = "",
                maxPrice = "",
                selectedLocations = emptyList(),
                selectedCondition = null,
                sortBy = "newest"
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Monash MP",
                        style = TextStyle(
                            fontFamily = pacificoFont,
                            fontSize = 24.sp,
                            color = Color(0xFF006DAE)
                        )
                    )
                }
            )
        },
        bottomBar = { BottomNavBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Item */ },
                containerColor = Color(0xFF3167B2),
                shape = CircleShape,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item"
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onFilterClick = { println("Filter Clicked") }
            )
            CategoryChips(selectedCategory = selectedCategory)
            Box(modifier = Modifier.weight(1f)) {
                ItemGrid()
            }
        }
        if (showFilter) {
            ModalBottomSheet(
                onDismissRequest = { showFilter = false },
                sheetState = sheetState
            ) {
                FilterDialog(
                    filterData = filterData,  // 存放在 remember 里面的
                    onMinPriceChange = { filterData = filterData.copy(minPrice = it) },
                    onMaxPriceChange = { filterData = filterData.copy(maxPrice = it) },
                    onLocationCheckedChange = { location ->
                        filterData = filterData.copy(
                            selectedLocations = if (location in filterData.selectedLocations)
                                filterData.selectedLocations - location
                            else
                                filterData.selectedLocations + location
                        )
                    },
                    onConditionChange = { filterData = filterData.copy(selectedCondition = it) },
                    onSortByChange = { filterData = filterData.copy(sortBy = it) },
                    onReset = {
                        filterData = FilterData("", "", emptyList(), null, "newest")
                    },
                    onApply = {
                        showFilter = false
                        println("筛选数据: $filterData")
                    }
                )
            }
        }

    }
}



