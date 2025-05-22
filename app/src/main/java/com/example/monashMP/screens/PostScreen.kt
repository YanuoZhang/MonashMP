
package com.example.monashMP.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.monashMP.components.BottomNavBar
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.ItemDetailSection
import com.example.monashMP.components.PhotoUploadSection
import com.example.monashMP.components.PostContactInfoSection
import com.example.monashMP.components.PostTransactionPreferenceSection
import com.example.monashMP.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.gestures.*
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt


/**
 * Screen for posting a new product listing.
 */
@Composable
fun PostScreen(
    viewModel: ProductViewModel,
    navController: NavHostController,
    onPostResult: (Boolean) -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()
    val context = LocalContext.current

    // Automatically update email and navigate on success
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

            DraggableSaveDraftButton(
                onClick = {
                    viewModel.saveDraft(context)
                }
            )

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

@Composable
fun DraggableSaveDraftButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(40f) }
    var offsetY by remember { mutableStateOf(0f) }

    var parentWidth by remember { mutableStateOf(0) }
    var parentHeight by remember { mutableStateOf(0) }
    var buttonWidth by remember { mutableStateOf(0) }
    var buttonHeight by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                parentWidth = it.size.width
                parentHeight = it.size.height
            }
            .padding(0.dp)
    ) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(Icons.Default.Save, contentDescription = "Save draft")
            },
            text = { Text("Save Draft") },
            onClick = onClick,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .onGloballyPositioned {
                    buttonWidth = it.size.width
                    buttonHeight = it.size.height
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = offsetX + dragAmount.x
                        val newY = offsetY + dragAmount.y

                        offsetX = newX.coerceIn(0f, (parentWidth - buttonWidth).toFloat())
                        offsetY = newY.coerceIn(0f, (parentHeight - buttonHeight).toFloat())
                    }
                }
        )
    }
}



