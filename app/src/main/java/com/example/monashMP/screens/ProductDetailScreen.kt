package com.example.monashMP.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.monashMP.utils.formatTimestamp
import com.example.monashMP.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Displays detailed information about a product, including photos, seller info,
 * transaction preferences, and a map view of the meetup location.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ProductViewModel,
    navController: NavHostController
) {
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    // Trigger fetch when screen loads
    LaunchedEffect(Unit) {
        viewModel.incrementViewCount(productId)
        viewModel.fetchProduct(productId)
        if (currentUserUid != null) {
            viewModel.checkFavoriteStatus(currentUserUid, productId)
        }
    }

    val product by viewModel.product.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val sellerInfo by viewModel.sellerInfo.collectAsState()

    Scaffold(
        topBar = {
            CommonTopBar(title = "Product Detail", onBackClick = {
                navController.popBackStack()
            })
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {

                ImageGallery(
                    images = product?.photos ?: emptyList(),
                    isFavorite = isFavorite
                )
            }
            item {
                ProductInfoSection(
                    title = product?.title ?: "--",
                    price = "$${product?.price ?: "--"}",
                    condition = product?.condition ?: "--",
                    views = product?.viewCount ?: 0,
                    postedDate = product?.createdAt?.formatTimestamp() ?: "--"
                )
            }
            item {
                DescriptionSection(
                    intro = product?.desc ?: "--",
                    extraNotes = product?.additionalNotes ?: ""
                )
            }
            item {
                LocationSection(location = product?.location ?: "--")
            }
            item {
                SellerInfoSection(
                    avatarUrl = sellerInfo?.avatarUrl,
                    name = sellerInfo?.nickname ?: product?.email ?: "--",
                    memberSince = sellerInfo?.createdAt?.formatTimestamp() ?: "--"
                )
                NoteCard(email = product?.email ?: "--")
            }
            item {
                MapSection(
                    campusName = product?.location ?: "--",
                    address = "${product?.location ?: "--"} ${product?.meetupPoint ?: "--"}" ,
                    mapImageResId = R.drawable.map,
                )
            }
            item {
                val prefs = listOf(
                    Icons.Default.LocationOn to (product?.meetupPoint ?: "--"),
                    Icons.Default.Money to (product?.paymentMethodPreference ?: "--"),
                    Icons.Default.AccessTime to viewModel.buildDayPreference(product)
                )
                TransactionPreferenceSection(preferences = prefs)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (currentUserUid != null && product != null) {
                            viewModel.toggleFavorite(currentUserUid, product!!.productId)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(if (isFavorite) "Unsave" else "Save")
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
