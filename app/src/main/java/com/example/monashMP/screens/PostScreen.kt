package com.example.monashMP.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.ItemDetailSection
import com.example.monashMP.components.PhotoUploadSection
import com.example.monashMP.components.PostContactInfoSection
import com.example.monashMP.components.PostTransactionPreferenceSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen() {
//    val navController = rememberNavController() // 如果有导航需求
    var photoUris by remember { mutableStateOf(listOf<Uri>()) }
    Scaffold(
        topBar = { CommonTopBar(
            onBackClick = { /*TODO*/ },
            title = "Post"
        ) },
        bottomBar = { BottomNavBar() }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp))
        {
            Spacer(modifier = Modifier.height(16.dp))
//            PhotoUploadSection()
            PhotoUploadSection(
                photos = photoUris,
                onAddPhotos = { selected -> photoUris = selected }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ItemDetailSection()
            Spacer(modifier = Modifier.height(24.dp))
            PostTransactionPreferenceSection()
            Spacer(modifier = Modifier.height(24.dp))
            PostContactInfoSection()

            // Post Listing
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /*TODO*/ },
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3167B2),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3167B2).copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Post Listing")
            }
        }
    }
}

