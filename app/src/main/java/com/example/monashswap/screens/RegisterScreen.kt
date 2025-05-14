package com.example.monashswap.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monashswap.R
import com.example.monashswap.components.BirthdatePickerField
import com.example.monashswap.components.CommonTopBar
import com.example.monashswap.components.GenericDropdownField
import com.example.monashswap.components.Label
import com.example.monashswap.components.RequiredLabel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen() {
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var nicknameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val primaryCampus = listOf("Clayton", "Caulfield", "Peninsula", "Parkville", "Malaysia")
    var selectedPrimaryCampus by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { CommonTopBar(
            onBackClick = { /*TODO*/ },
            title = "Sign-up"
        ) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 50.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(48.dp))

                Text("Complete Your Profile", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Text("Please set up your account details", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(32.dp))
                // Avatar Placeholder
                Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF006DAE))
                            .border(2.dp, Color(0xFF006DAE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_sample),
                            contentDescription = "title",
                            contentScale = ContentScale.Crop, // 裁剪填充
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF006DAE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Add Photo", color = Color(0xFF006DAE), fontWeight = FontWeight.Medium)
            }


            Spacer(modifier = Modifier.height(32.dp))

            // Nickname Field
            RequiredLabel("Nickname")
            OutlinedTextField(
                value = nickname,
                onValueChange = {
                    nickname = it
                    nicknameError = if (it.isBlank()) "Nickname is required" else ""
                },
                isError = nicknameError.isNotEmpty(),
                placeholder = { Text("Enter your nickname") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (nicknameError.isNotEmpty()) {
                Text(nicknameError, color = Color.Red, fontSize = 12.sp)
            }
            Text("This will be displayed to other users", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Birthdate
            BirthdatePickerField(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            //Primary Campus
            GenericDropdownField(
                labelContent = { Label("Primary Campus") },
                options = primaryCampus,
                selectedOption = selectedPrimaryCampus,
                onOptionSelected = { selectedPrimaryCampus = it },
                optionTextProvider = { it }
            )
            Text("Which Monash campus do you primarily attend?", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            // Password Field
            RequiredLabel("Password")
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (it.length < 8) "Password must be at least 8 characters" else ""
                },
                isError = passwordError.isNotEmpty(),
                placeholder = { Text("Create password") },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (passwordError.isNotEmpty()) {
                Text(passwordError, color = Color.Red, fontSize = 12.sp)
            }
            Text("Must be at least 8 characters with letters, numbers, and special characters", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            RequiredLabel("Confirm Password")
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = if (it != password) "Passwords do not match" else ""
                },
                isError = confirmPasswordError.isNotEmpty(),
                placeholder = { Text("Confirm password") },
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (confirmPasswordError.isNotEmpty()) {
                Text(confirmPasswordError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = { /* TODO: Submit form */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = nickname.isNotBlank() && password.length >= 8 && password == confirmPassword,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006DAE))
            ) {
                Text("Complete Profile", color = Color.White, fontSize = 18.sp)
            }
        }
    }

}
