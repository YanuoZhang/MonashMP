package com.example.monashswap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CategoryChips(
    selectedCategory: MutableState<String>
) {
    val categories = listOf("All", "Electronics", "Books", "Furniture", "Clothing", "Sports")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory.value

            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) Color(0xFF006DAE) else Color(0xFFF1F3F5),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { selectedCategory.value = category }
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
