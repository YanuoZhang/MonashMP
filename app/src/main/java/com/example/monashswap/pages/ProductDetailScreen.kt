package com.example.monashswap.pages

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.monashswap.R
import com.example.monashswap.components.BottomNavBar
import com.example.monashswap.components.CommonTopBar
import com.example.monashswap.components.DescriptionSection
import com.example.monashswap.components.ImageGallery
import com.example.monashswap.components.LocationSection
import com.example.monashswap.components.MapSection
import com.example.monashswap.components.NoteCard
import com.example.monashswap.components.ProductInfoSection
import com.example.monashswap.components.SellerInfoSection
import com.example.monashswap.components.TransactionPreferenceSection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen() {
//    var isSaved by remember { mutableStateOf(false) }
//    var showContactSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true
//    )
//    var showSheet by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { CommonTopBar(
            onBackClick = { /*TODO*/ },
            title = "Product Detail"
        ) },
        bottomBar = {
            BottomNavBar()
//            DetailBottomBar(
//                isSaved = isSaved,
//                onSaveClick = { isSaved = !isSaved },
//                onContactClick = { /* 跳转或弹窗 */ }
//            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item { ImageGallery() }
            item {
                ProductInfoSection(
                    title = "Engineering Textbooks",
                    price = "$85",
                    condition = "Like New",
                    views = 42,
                    postedDate = "Apr 10, 2025"
                )
            }
            item {
                DescriptionSection(
                    intro = "Selling a set of engineering textbooks in excellent condition. All books are like new with no highlights or markings. Perfect for engineering students at Monash.",
                    bookList = listOf(
                        "Fundamentals of Engineering Mechanics (8th Edition)",
                        "Introduction to Electrical Engineering (5th Edition)",
                        "Materials Science and Engineering (10th Edition)",
                        "Engineering Mathematics (4th Edition)"
                    ),
                    extraNotes = "All books are the latest editions and were purchased new last semester. They're in perfect condition as I mostly used digital resources."
                )
            }
            item {
                LocationSection( location = "Caulfield Campus, Monash University")
            }
            item {
                SellerInfoSection(
                    avatarResId = R.drawable.avatar_sample, // 你的本地头像
                    name = "Daniel Chen",
                    rating = 4.7,
                    reviews = 23,
                    memberSince = "September 2023",
                    onViewProfileClick = { /* TODO: 跳转 profile */ }
                )
                NoteCard()
            }
            item {
                MapSection(
                    campusName = "Caulfield Campus",
                    address = "900 Dandenong Rd, Caulfield East",
                    mapImageResId = R.drawable.map
                )
            }
            item {
                TransactionPreferenceSection(
                    preferences = listOf(
                        Icons.Default.LocationOn to "Clayton Meetup",
                        Icons.Default.Money to "Preferred Cash",
                        Icons.Default.AccessTime to "Weekend"
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

//    if (showSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showSheet = false },
//            sheetState = sheetState,
//        ) {
//            ContactSellerSection(
//                sellerAvatarResId = R.drawable.avatar_sample,
//                sellerName = "Daniel Chen",
//                onDismiss = { showContactSheet = false },
//                onSendMessage = { msg ->
//                        // TODO: 处理发送逻辑
//                }
//            ) // 抽出来的内容 UI
//        }
//    }
}
