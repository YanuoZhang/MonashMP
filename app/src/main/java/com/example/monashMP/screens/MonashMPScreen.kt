package com.example.monashMP.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.monashMP.R
import com.example.monashMP.components.AddItemFAB
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.FilterBottomSheet
import com.example.monashMP.components.FilterData
import com.example.monashMP.components.HomeTopBar
import com.example.monashMP.components.MainContent
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.viewmodel.HomeViewModel
import com.example.monashMP.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonashMPScreen(
    navController: NavHostController,
    repository: ProductRepository,
) {

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )
    val pacificoFont = FontFamily(Font(R.font.lilita_one))

    val filterState by viewModel.filterState.collectAsState()
    val showFilter = viewModel.showFilterSheet.value
    val products by viewModel.filteredProducts.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    Scaffold(
        topBar = { HomeTopBar(pacificoFont) },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = { AddItemFAB() },
    ) { paddingValues ->
        MainContent(
            query = filterState.query,
            onQueryChange = viewModel::updateQuery,
            onFilterClick = viewModel::toggleFilterSheet,
            selectedCategory = filterState.category,
            onCategoryChange = viewModel::updateCategory,
            modifier = Modifier.padding(paddingValues),
            productList = products
        )

        if (showFilter) {
            FilterBottomSheet(
                sheetState = sheetState,
                filterData = FilterData(
                    minPrice = filterState.minPrice.toString(),
                    maxPrice = filterState.maxPrice.toString(),
                    selectedLocations = filterState.locations,
                    selectedCondition = filterState.condition,
                    sortBy = filterState.sortBy
                ),
                onUpdateFilter = viewModel::updateFilterData,
                onClose = viewModel::toggleFilterSheet,
                onApply = {},
                onReset = viewModel::resetFilter
            )
        }
    }
}