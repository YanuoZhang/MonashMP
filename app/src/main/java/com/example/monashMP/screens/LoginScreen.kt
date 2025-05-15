package com.example.monashMP.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monashMP.R
import com.example.monashMP.components.ErrorToast
import com.example.monashMP.model.LoginState
import com.example.monashMP.viewmodel.LoginViewModel
import com.example.monashMP.viewmodel.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    onRegisterClick: (String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )
    val loginState by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

//    val signInClient = remember { Identity.getSignInClient(context) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                val email = account.email ?: ""

                if (idToken != null && email.endsWith("@student.monash.edu")) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener {
                            viewModel.checkIfUserExists(email) { exists ->
                                if (exists) {
                                    onLoginSuccess()
                                } else {
                                    onRegisterClick(email)
                                }
                            }
                        }
                } else {
                    errorMessage = "Only Monash student emails are allowed"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                errorMessage = "Google Sign-In failed"
            }
        }
    }
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartIntentSenderForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            try {
//                val credential = signInClient.getSignInCredentialFromIntent(result.data)
//                val idToken = credential.googleIdToken
//                val emailFromToken = credential.id
//
//                if (idToken != null && emailFromToken.endsWith("@student.monash.edu")) {
//                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//                    FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
//                        .addOnSuccessListener {
//                            viewModel.checkIfUserExists(emailFromToken) { exists ->
//                                if (exists) {
//                                    onLoginSuccess()
//                                } else {
//                                    onRegisterClick(emailFromToken)
//                                }
//                            }
//                        }
//                        .addOnFailureListener {
//                            errorMessage = "Firebase 登录失败"
//                        }
//                } else {
//                    errorMessage = "Only Monash student emails are allowed"
//                }
//            } catch (e: Exception) {
//                errorMessage = "Google Sign-In failed"
//            }
//        }
//    }

    fun launchGoogleSignInOldWay(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

//    fun launchGoogleSignIn() {
//        val request = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setFilterByAuthorizedAccounts(false)
//                    .setServerClientId(context.getString(R.string.default_web_client_id)) // ✅ 必须是 Web client ID
//                    .build()
//            )
//            .setAutoSelectEnabled(false)
//            .build()
//
//        signInClient.beginSignIn(request)
//            .addOnSuccessListener { result ->
//                val senderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
//                launcher.launch(senderRequest)
//            }
//            .addOnFailureListener {
//                errorMessage = "Google 登录启动失败"
//            }
//    }

    when (loginState) {
        LoginState.DEFAULT -> errorMessage = ""
        LoginState.SUCCESS -> {
            isLoading = false
            onLoginSuccess()
        }
        LoginState.FAILURE -> {
            isLoading = false
            errorMessage = "Email or password is incorrect"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text("MonashTrade", fontSize = 40.sp, color = Color(0xFF006DAE), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Welcome to MonashTrade", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "Trade safely within the Monash community",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "Login Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isLoading = true
                    viewModel.login(email, password)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006DAE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Sign In", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text("or", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    launchGoogleSignInOldWay(context, launcher)
                },
//                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "title",
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Google")
            }
            Text(
                "* First-time users, please sign-in with your Monash google account to register",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        ErrorToast(
            message = errorMessage,
            onDismiss = { errorMessage = "" }
        )

//        if (errorMessage.isNotEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.BottomCenter)
//                    .background(Color.White, shape = RoundedCornerShape(8.dp))
//                    .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
//                    .padding(12.dp)
//            ) {
//                Text(errorMessage, color = Color.Red, textAlign = TextAlign.Center, fontSize = 14.sp)
//            }
//        }
    }
}
