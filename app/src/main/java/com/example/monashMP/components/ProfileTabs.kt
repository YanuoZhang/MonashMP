package com.example.monashMP.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun ProfileTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = Color(0xFF006DAE),
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Color(0xFF006DAE)
            )
        }
    ) {
        Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) }) {
            Text(
                "Saved Items",
                modifier = Modifier.padding(16.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    color = if (selectedTab == 0) Color(0xFF006DAE) else Color.Gray
                )
            )
        }
        Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) }) {
            Text(
                "My Listings",
                modifier = Modifier.padding(16.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    color = if (selectedTab == 1) Color(0xFF006DAE) else Color.Gray
                )
            )
        }
    }
}
