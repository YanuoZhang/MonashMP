package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.monashMP.data.entity.ProductEntity

@Composable
fun ProductGrid(
    productList: List<ProductEntity>,
    navController: NavHostController,
    favoriteIds: List<Long>
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productList) { item ->
            ProductCard(
                item,
                onClick = { navController.navigate("productDetail/${item.productId}") },
                isFavorite = favoriteIds.contains(item.productId)
            )
        }
    }
}