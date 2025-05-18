package com.example.monashMP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.monashMP.data.model.ProfileItem


@Composable
fun ProfileGrid(
    items: List<ProfileItem>,
    onProductCardClick: (Long) -> Unit,
    onDeleteClick: (ProfileItem) -> Unit = {}
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
                onCardClick = { onProductCardClick(it.id) },
                onDeleteClick = onDeleteClick,
            )
        }
    }
}
