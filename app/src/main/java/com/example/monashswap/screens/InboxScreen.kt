package com.example.monashswap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashswap.components.BottomNavBar
import com.example.monashswap.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(/*messages: List<Message>*/) {
    val messages = listOf(
        Message(
            senderName = "Emma Wilson",
            senderEmail = "emmawilson@gmail.com",
            time = "Today, 10:45 AM",
            title = "Vintage Leather Jacket - Size M",
            content = "Hi there! I'm interested in your leather jacket. Is it still available? I was wondering if you could provide more details about the condition and if there are any signs of wear. Thanks!"
        ),
        Message(
            senderName = "Michael Chen",
            senderEmail = "michaelchen@outlook.com",
            time = "Yesterday, 3:22 PM",
            title = "Sony PlayStation 5 - Like New",
            content = "Hello, I'm interested in your PS5. Would you be willing to negotiate on the price? Also, does it come with any games or extra controllers? I could pick it up tomorrow if we can agree on terms."
        ),
        Message(
            senderName = "Sophia Rodriguez",
            senderEmail = "sophiarodriguez@yahoo.com",
            time = "Apr 14, 5:17 PM",
            title = "Antique Oak Dining Table",
            content = "I love the dining table you posted! Could you please send me the exact dimensions? Also, would you be able to arrange delivery to downtown? I don't have a vehicle large enough to transport it myself."
        ),
        Message(
            senderName = "James Thompson",
            senderEmail = "jamesthompson@gmail.com",
            time = "Apr 12, 9:03 AM",
            title = "Mountain Bike - Trek X-Caliber 8",
            content = "Hey there! I'm very interested in your mountain bike. Has it had any major repairs or replacements? I'd like to take it for a test ride if possible. Are you available this weekend for me to come by?"
        ),
        Message(
            senderName = "Olivia Parker",
            senderEmail = "oliviaparker@hotmail.com",
            time = "Apr 10, 2:45 PM",
            title = "iPhone 13 Pro - 256GB",
            content = "Hi! I'm interested in your iPhone. What's the battery health percentage? Also, does it come with the original box and accessories? I'm looking to upgrade from my current phone and this looks perfect."
        ),
        Message(
            senderName = "Daniel Kim",
            senderEmail = "danielkim@gmail.com",
            time = "Apr 8, 11:30 AM",
            title = "Acoustic Guitar - Martin DX1AE",
            content = "Hello! I've been looking for a Martin guitar for a while now. Could you tell me how old it is and if there are any issues with it? I'm a serious player and would love to add this to my collection if it's in good condition."
        )
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Inbox") },
                    navigationIcon = {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E7FF))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color(0xFF4F46E5))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("To contact senders, please use their email address", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        bottomBar = { BottomNavBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageCard(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageCard(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
            .clickable { /* Navigate */ }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(message.senderName, fontWeight = FontWeight.Bold)
                Text(message.senderEmail, fontSize = 12.sp, color = Color.Gray)
            }
            Text(message.time, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(message.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(message.content, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
    }
}
