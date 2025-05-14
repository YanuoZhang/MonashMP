package com.example.monashswap.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashswap.R
import com.example.monashswap.components.BottomNavBar
import com.example.monashswap.components.CommonTopBar
import com.example.monashswap.model.ProfileItem
import com.example.monashswap.model.ProfileItemType

@Composable
fun ProfileScreen() {

    val postedItems = listOf(
        ProfileItem("MacBook Pro 2023", "$1200", "Clayton", ProfileItemType.Saved),
        ProfileItem("Engineering Textbooks", "$85", "Caulfield", ProfileItemType.Saved),
        ProfileItem("Ergonomic Desk Chair", "$120", "Clayton", ProfileItemType.Saved),
        ProfileItem("TI-84 Calculator", "$60", "Parkville", ProfileItemType.Saved),
        ProfileItem("Mountain Bike", "$250", "Clayton", ProfileItemType.Saved),
        ProfileItem("Modern Desk Lamp", "$35", "Caulfield", ProfileItemType.Saved),
        ProfileItem("Modern Desk Lamp", "$35", "Caulfield", ProfileItemType.Saved),
        ProfileItem("Modern Desk Lamp", "$35", "Caulfield", ProfileItemType.Saved),
        ProfileItem("Modern Desk Lamp", "$35", "Caulfield", ProfileItemType.Saved),
        ProfileItem("Modern Desk Lamp", "$35", "Caulfield", ProfileItemType.Saved)
    )

    Scaffold(
        topBar = {
            CommonTopBar(
                onBackClick = { /*TODO*/ },
                title = "My Profile"
            )
        },
        bottomBar = { BottomNavBar() },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            ProfileHeader()
            HorizontalDivider( thickness = 1.dp, color = Color(0xFFE5E7EB) )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Posted Items",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006DAE),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(2.dp)
                        .background(Color(0xFF006DAE))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(postedItems, key = { it.title }) { item ->
                    ProfileItemCard(
                        item,
                        onDeleteClick = { item ->
                            // 删除
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar_sample),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Emily Johnson", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("emily.johnson@gmail.com", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
//            TextItem("24", "Posted")
//            TextItem("36", "Saved")
//            TextItem("18", "Sold")
        }
    }
}
@Composable
fun TextItem(number: String, title: String)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(number, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(title, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun ProfileItemCard(
    item: ProfileItem,
    onDeleteClick: (ProfileItem) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.test),
                contentDescription = item.title,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                    .clickable {
                        onDeleteClick(item)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = 0.9f))
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                    maxLines = 1
                )
                Text(
                    text = item.price,
                    color = Color(0xFF006DAE),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

