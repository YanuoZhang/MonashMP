package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.monashMP.data.model.ProfileItem
import com.example.monashMP.viewmodel.ProductViewModel


@Composable
fun ProfileGrid(
    items: List<ProfileItem>,
    onProductCardClick: (Long, Boolean) -> Unit,
    viewModel: ProductViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ProfileItemCard(
                item,
                onCardClick = { onProductCardClick(it.id, it.isDraft) },
                onDeleteClick =  {
                    if (it.isDraft) {
                        viewModel.deleteDraftProduct(it.id)
                    } else {
                        viewModel.deleteProduct(it.id)
                    }
                }
            )
        }
    }
}
