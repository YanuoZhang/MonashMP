package com.example.monashMP.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun BottomNavBar() {
    val selectedTab = remember { mutableStateOf("Home") }

    val tabs = listOf(
        "Home" to Icons.Default.Home,
//        "Saved" to Icons.Default.FavoriteBorder,
        "Post" to Icons.Default.AddCircle,
//        "Inbox" to Icons.Default.Notifications,
        "Profile" to Icons.Default.Person
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 2.dp
    ) {
        tabs.forEach { (label, icon) ->
            NavigationBarItem(
                selected = selectedTab.value == label,
                onClick = { selectedTab.value = label },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selectedTab.value == label) Color(0xFF3167B2) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (selectedTab.value == label) Color(0xFF3167B2) else Color.Gray,
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}