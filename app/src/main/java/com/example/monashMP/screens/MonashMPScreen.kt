package com.example.monashMP.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavHostController
import com.example.monashMP.R
import com.example.monashMP.components.AddItemFAB
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.FilterBottomSheet
import com.example.monashMP.components.HomeTopBar
import com.example.monashMP.components.MainContent
import com.example.monashMP.model.FilterData
import com.example.monashMP.viewmodel.ProductViewModel

/**
 * MonashMPScreen is the main screen showing filtered product list with filter functionality.
 * It wraps MainContent with a top bar, bottom navigation, FAB, and a modal filter sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonashMPScreen(
    navController: NavHostController,
    viewModel: ProductViewModel
) {
    val pacificoFont = FontFamily(Font(R.font.lilita_one))
    val filterState by viewModel.filterState.collectAsState()
    val showFilter by viewModel.showFilterSheet.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val favoriteIds by viewModel.favoriteProductIds.collectAsState()

    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteIds()
        viewModel.loadFilteredProducts()
    }

    Scaffold(
        topBar = { HomeTopBar(pacificoFont) },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = { AddItemFAB(navController) },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            MainContent(
                query = filterState.query,
                onQueryChange = {
                    viewModel.updateQuery(it)
                    viewModel.loadFilteredProducts()
                },
                onFilterClick = viewModel::toggleFilterSheet,
                selectedCategory = filterState.category,
                onCategoryChange = {
                    viewModel.updateCategory(it)
                    viewModel.loadFilteredProducts()
                },
                modifier = Modifier,
                productList = products,
                navController = navController,
                favoriteIds = favoriteIds
            )

            // Filter bottom sheet
            if (showFilter) {
                FilterBottomSheet(
                    sheetState = sheetState,
                    filterData = FilterData(
                        minPrice = filterState.minPrice,
                        maxPrice = filterState.maxPrice,
                        selectedLocations = filterState.locations,
                        selectedCondition = filterState.condition,
                        sortBy = filterState.sortBy
                    ),
                    onUpdateFilter = viewModel::updateFilterData,
                    onClose = viewModel::toggleFilterSheet,
                    onApply = {
                        viewModel.loadFilteredProducts()
                        viewModel.toggleFilterSheet()
                    },
                    onReset = {
                        viewModel.resetFilter()
                        viewModel.loadFilteredProducts()
                    }
                )

            }
        }
    }
}
