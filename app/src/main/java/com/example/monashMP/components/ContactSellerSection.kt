package com.example.monashMP.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSellerSection(
    sellerAvatarResId: Int,
    sellerName: String,
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = "Contact Seller",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Note: The seller cannot reply in this app. Please check your email for any response.", fontSize = 14.sp)
                Text("Or contact the seller directly via email: ", fontSize = 14.sp)
                Text("emily.seller@example.com", fontWeight = FontWeight.Medium, color = Color(0xFF3B82F6))
            }
        }
        // 卖家头像 + 名字
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = sellerAvatarResId),
                contentDescription = "Seller Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)) // gray-200
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = sellerName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Usually responds within 1 hour",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 输入框
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Hi, is this item still available?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 底部发送按钮
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    onSendMessage(message)
                    onDismiss()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Leave Message")
            }
        }

    }
}
