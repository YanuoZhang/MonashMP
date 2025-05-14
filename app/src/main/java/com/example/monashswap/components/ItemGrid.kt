package com.example.monashswap.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.monashswap.model.Item

@Composable
fun ItemGrid() {
    val items = listOf(
        Item("MacBook Pro 2023", "$1200", "Clayton"),
        Item("Engineering Textbooks", "$85", "Caulfield"),
        Item("Ergonomic Desk Chair", "$120", "Clayton"),
        Item("TI-84 Calculator", "$60", "Parkville"),
        Item("Mountain Bike", "$250", "Clayton"),
        Item("Modern Desk Lamp", "$35", "Caulfield"),
        Item("Modern Desk Lamp", "$35", "Caulfield"),
        Item("Modern Desk Lamp", "$35", "Caulfield"),
        Item("Modern Desk Lamp", "$35", "Caulfield"),
        Item("Modern Desk Lamp", "$35", "Caulfield")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            ItemCard(item)
        }
    }
}