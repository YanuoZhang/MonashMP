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
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.rememberAsyncImagePainter
import com.example.monashMP.R
import com.example.monashMP.model.UserModel


@Composable
fun ProfileHeader(
    userInfo: UserModel?,
    favoriteCount: Int,
    postCount: Int,
) {

    val painter = rememberAsyncImagePainter(
        model = userInfo?.avatarUrl,
        fallback = painterResource(R.drawable.avatar_sample),
        error = painterResource(R.drawable.avatar_sample)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(userInfo?.nickName ?: "--", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(userInfo?.email ?: "--", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextItem("$favoriteCount", "Saved")
            TextItem("$postCount", "Posted")
        }
    }
}
@Composable
fun TextItem(number: String, title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(number, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(title, color = Color.Gray, fontSize = 12.sp)
    }
}