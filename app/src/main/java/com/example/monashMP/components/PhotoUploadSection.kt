
package com.example.monashMP.components

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.monashMP.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun PhotoUploadSection(viewModel: ProductViewModel) {
    val photos by viewModel.formState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                bitmap?.let { bmp ->
                    coroutineScope.launch {
                        try {
                            val index = photos.photos.size
                            val productId = viewModel.getTempProductId()
                            val url = viewModel.uploadAndGetPhotoUrl(productId, index, bmp)
                            viewModel.addPhoto(url)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RequiredLabel("Photo")
            Text(
                "${photos.photos.size}/5 photos added",
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clickable {
                    if (photos.photos.size < 5) {
                        permissionLauncher.launch(storagePermission)
                    } else {
                        Toast.makeText(context, "Max 5 photos", Toast.LENGTH_SHORT).show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.LightGray)
                Text("Add Photos", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Images will be compressed (max 1000px). Up to 5 allowed.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        if (photos.photos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos.photos) { url ->
                    Box(modifier = Modifier.size(80.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Uploaded Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.Red,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    viewModel.removePhoto(url)
                                }
                        )
                    }
                }
            }
        }

        fieldErrors["photos"]?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
