package com.example.monashMP.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.monashMP.data.entity.ProductEntity

@Composable
fun MainContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    productList: List<ProductEntity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onFilterClick = onFilterClick
        )
        CategoryChips(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategoryChange
        )
        Box(modifier = Modifier.weight(1f)) {
            ProductGrid(productList)
        }
    }
}