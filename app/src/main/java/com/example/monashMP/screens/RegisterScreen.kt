package com.example.monashMP.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashMP.components.AvatarPreview
import com.example.monashMP.components.BirthdatePickerField
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.GenericDropdownField
import com.example.monashMP.components.Label
import com.example.monashMP.components.RequiredLabel
import com.example.monashMP.viewmodel.AuthViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    email: String,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.registerState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.setRegisterEmail(email)
    }

    Scaffold(topBar = {
        CommonTopBar(
            onBackClick = onBackClick,
            title = "Sign-up"
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 50.dp)
        ) {
            val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }

            val imagePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        viewModel.setAvatar(bitmap)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Image load failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) imagePicker.launch("image/*")
                else Toast.makeText(context, "Permission needed to select photo", Toast.LENGTH_SHORT).show()
            }

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(48.dp))
                Text("Complete Your Profile", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Text("Please set up your account details", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clickable { permissionLauncher.launch(storagePermission) },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    AvatarPreview(bitmap = uiState.avatarBitmap)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Add Photo", color = Color(0xFF006DAE), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(32.dp))
            RequiredLabel("Nickname")
            OutlinedTextField(
                value = uiState.nickname,
                onValueChange = viewModel::onNicknameChanged,
                isError = uiState.nicknameError.isNotEmpty(),
                placeholder = { Text("Enter your nickname") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (uiState.nicknameError.isNotEmpty()) {
                Text(uiState.nicknameError, color = Color.Red, fontSize = 12.sp)
            }
            Text("This will be displayed to other users", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            BirthdatePickerField(
                selectedDate = uiState.birthday,
                onDateSelected = viewModel::onBirthdayChanged
            )

            Spacer(modifier = Modifier.height(16.dp))
            GenericDropdownField(
                labelContent = { Label("Primary Campus") },
                options = uiState.campusOptions,
                selectedOption = uiState.primaryCampus,
                onOptionSelected = viewModel::onCampusChanged,
                optionTextProvider = { it }
            )
            Text("Which Monash campus do you primarily attend?", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            RequiredLabel("Password")
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                isError = uiState.passwordError.isNotEmpty(),
                placeholder = { Text("Create password") },
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibilityRegister) {
                        Icon(if (uiState.showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (uiState.passwordError.isNotEmpty()) {
                Text(uiState.passwordError, color = Color.Red, fontSize = 12.sp)
            }
            Text("Must be at least 8 characters with letters, numbers, and special characters", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            RequiredLabel("Confirm Password")
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                isError = uiState.confirmPasswordError.isNotEmpty(),
                placeholder = { Text("Confirm password") },
                trailingIcon = {
                    IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                        Icon(if (uiState.showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                visualTransformation = if (uiState.showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (uiState.confirmPasswordError.isNotEmpty()) {
                Text(uiState.confirmPasswordError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.submit(onRegisterSuccess) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = uiState.isSubmitEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006DAE))
            ) {
                if (uiState.isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("Complete Profile", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
