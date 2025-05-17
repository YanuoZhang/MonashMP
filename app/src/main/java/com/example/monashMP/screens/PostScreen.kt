package com.example.monashMP.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.ItemDetailSection
import com.example.monashMP.components.PhotoUploadSection
import com.example.monashMP.components.PostContactInfoSection
import com.example.monashMP.components.PostTransactionPreferenceSection
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.viewmodel.PostViewModel
import com.example.monashMP.viewmodel.PostViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PostScreen(
    navController: NavHostController,
    repository: ProductRepository,
    onPostResult: (Boolean) -> Unit
) {
    val viewModel: PostViewModel = viewModel(factory = PostViewModelFactory(repository))
    val formState by viewModel.formState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()

    LaunchedEffect(postSuccess) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (!email.isNullOrBlank()) {
            viewModel.updateTextField("email", email)
        }

        if (postSuccess) {
            onPostResult(true)
            navController.navigate("home") { popUpTo("post") { inclusive = true } }
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                onBackClick = { navController.popBackStack() },
                title = "Post"
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                PhotoUploadSection(viewModel)

                ItemDetailSection(
                    formState = formState,
                    onFieldChange = viewModel::updateTextField,
                    errors = fieldErrors
                )

                PostTransactionPreferenceSection(
                    formState = formState,
                    meetupOptions = viewModel.meetupPointDatasource,
                    onFieldChange = viewModel::updateTextField,
                    errors = fieldErrors
                )

                PostContactInfoSection(
                    formState = formState,
                    onFieldChange = viewModel::updateTextField,
                    errors = fieldErrors
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.postProduct() },
                    enabled = fieldErrors.isEmpty() && !isPosting,
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
                    if (isPosting) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                    else Text("Post Listing")
                }
            }

            if (isPosting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
