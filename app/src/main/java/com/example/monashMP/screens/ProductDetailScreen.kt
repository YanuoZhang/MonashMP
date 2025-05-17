package com.example.monashMP.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.monashMP.R
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.DescriptionSection
import com.example.monashMP.components.ImageGallery
import com.example.monashMP.components.LocationSection
import com.example.monashMP.components.MapSection
import com.example.monashMP.components.NoteCard
import com.example.monashMP.components.ProductInfoSection
import com.example.monashMP.components.SellerInfoSection
import com.example.monashMP.components.TransactionPreferenceSection
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.utils.formatTimestamp
import com.example.monashMP.viewmodel.ProductDetailViewModel
import com.example.monashMP.viewmodel.ProductDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(productId: Long, navController: NavHostController) {
    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).productDao()
    val viewModel: ProductDetailViewModel = viewModel(
        factory = ProductDetailViewModelFactory(dao)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchProduct(productId)
    }

    val product by viewModel.product.collectAsState()
    Scaffold(
        topBar = { CommonTopBar(
            onBackClick = {
                navController.popBackStack()
            },
            title = "Product Detail"
        ) },
         bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item { ImageGallery() }
            item {
                ProductInfoSection(
                    title = product?.title ?: "--",
                    price = "$${product?.price ?: "--"}",
                    condition = product?.condition ?: "--",
                    views = 0, // Replace with actual views if tracked
                    postedDate = product?.createdAt?.formatTimestamp() ?: "--"
                )
            }
            item {
                DescriptionSection(
                    intro = product?.desc ?: "--",
                    bookList = listOf(
                        "Fundamentals of Engineering Mechanics (8th Edition)",
                        "Introduction to Electrical Engineering (5th Edition)",
                        "Materials Science and Engineering (10th Edition)",
                        "Engineering Mathematics (4th Edition)"
                    ),
                    extraNotes = product?.additionalNotes ?: ""
                )
            }
            item {
                LocationSection( location = product?.location ?: "--")
            }
            item {
                SellerInfoSection(
                    avatarResId = R.drawable.avatar_sample, // 你的本地头像
                    name = product?.email ?: "--",
                    rating = 4.7,
                    reviews = 23,
                    memberSince = "September 2023",
                    onViewProfileClick = { /* TODO: 跳转 profile */ }
                )
                NoteCard()
            }
            item {
                MapSection(
                    campusName = product?.location ?: "--",
                    address = "900 Dandenong Rd, Caulfield East",
                    mapImageResId = R.drawable.map
                )
            }
            item {
                val prefs = mutableListOf<Pair<Any, String>>()
                prefs += Icons.Default.LocationOn to (product?.meetupPoint ?: "--")
                prefs += Icons.Default.Money to (product?.paymentMethodPreference ?: "--")
                val days = listOfNotNull(
                    if (product?.dayPreferenceWeekdays == true) "Weekdays" else null,
                    if (product?.dayPreferenceWeekends == true) "Weekends" else null
                ).joinToString(" & ").ifEmpty { "--" }
                prefs += Icons.Default.AccessTime to days

                TransactionPreferenceSection(preferences = prefs)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

}
