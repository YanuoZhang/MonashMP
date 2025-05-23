package com.example.monashMP.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.ProfileGrid
import com.example.monashMP.components.ProfileHeader
import com.example.monashMP.components.ProfileTabs
import com.example.monashMP.viewmodel.ProductViewModel
import com.example.monashMP.workermanager.SyncProductsWorker

@Composable
fun ProfileScreen(
    viewModel: ProductViewModel,
    navController: NavHostController,
    onProductCardClick: (Long, Boolean) -> Unit,
    onLogoutClick: () -> Unit
) {
    val selectedTab = remember { mutableIntStateOf(0) }

    val savedItems by viewModel.savedItems.collectAsState()
    val postedItems by viewModel.postedItems.collectAsState()
    val userInfo by viewModel.sellerInfo.collectAsState() // reused field
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        viewModel.loadSavedProducts()
        viewModel.loadMyProducts()
        viewModel.loadUserInfo()
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "My Profile",
                actions = {
                    Text(
                        text = "Logout",
                        modifier = Modifier
                            .clickable { onLogoutClick() }
                            .padding(16.dp)
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ProfileHeader(
                userInfo = userInfo,
                favoriteCount = savedItems.size,
                postCount = postedItems.size
            )
            ProfileTabs(
                selectedTab = selectedTab.intValue,
                onTabSelected = { selectedTab.intValue = it }
            )
            ProfileGrid(
                items = if (selectedTab.intValue == 0) savedItems else postedItems,
                onProductCardClick = onProductCardClick,
                viewModel = viewModel
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                val workRequest = OneTimeWorkRequestBuilder<SyncProductsWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .offset(x = 0.dp, y = (-120).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Sync Now"
            )
        }
    }
}
