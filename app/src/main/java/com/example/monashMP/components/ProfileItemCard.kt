package com.example.monashMP.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.model.ProfileItem
import com.example.monashMP.model.ProfileItemType

@Composable
fun ProfileItemCard(
    item: ProfileItem,
    onDeleteClick: (ProfileItem) -> Unit = {},
    onFavouriteClick: (ProfileItem) -> Unit = {},
    onCardClick: (ProfileItem) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onCardClick.invoke(item) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
//            Image(
//                bitmap = base64ToBitmap(item.cover)!!.asImageBitmap(),
//                contentDescription = item.title,
//                modifier = Modifier
//                    .fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                    .clickable {
                        if (item.type == ProfileItemType.Saved) {
                            onFavouriteClick(item)
                        } else {
                            onDeleteClick(item)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.type == ProfileItemType.Saved) Icons.Filled.Favorite else Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (item.type == ProfileItemType.Saved) Color.Red else Color.Gray
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