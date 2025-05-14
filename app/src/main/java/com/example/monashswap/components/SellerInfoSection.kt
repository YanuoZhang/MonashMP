package com.example.monashswap.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SellerInfoSection(
    avatarResId: Int,
    name: String,
    rating: Double,
    reviews: Int,
    memberSince: String,
    onViewProfileClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF9FAFB), // gray-50 背景
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seller Avatar
            Image(
                painter = painterResource(id = avatarResId),
                contentDescription = "Seller Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)) // gray-200
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Seller Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937) // gray-800
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    repeat(5) { index ->
//                        Icon(
//                            imageVector = Icons.Filled.Star,
//                            contentDescription = "Rating",
//                            tint = if (rating >= index + 1) Color(0xFFF6A800) else Color(0xFFFCD34D), // 黄色星星
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "$rating ($reviews reviews)",
//                        fontSize = 12.sp,
//                        color = Color(0xFF6B7280) // gray-500
//                    )
//                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Member since $memberSince",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

//            // View Profile 按钮
//            OutlinedButton(
//                onClick = onViewProfileClick,
//                border = BorderStroke(1.dp, Color(0xFF006DAE)),
//                colors = ButtonDefaults.outlinedButtonColors(
//                    contentColor = Color(0xFF006DAE)
//                ),
//                modifier = Modifier.height(32.dp)
//            ) {
//                Text(
//                    text = "View Profile",
//                    fontSize = 12.sp
//                )
//            }
        }
    }
}


