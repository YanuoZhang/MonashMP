package com.example.monashMP.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun AddItemFAB(navController: NavHostController) {
    FloatingActionButton(
        onClick = { navController.navigate("Post") },
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