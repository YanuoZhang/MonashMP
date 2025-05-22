package com.example.monashMP.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.model.LoginState
import com.example.monashMP.data.model.LoginUiState
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.utils.UserSessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for both login and registration logic.
 * Integrates Firebase Authentication and Firebase Realtime Database.
 */
class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // --- Login State ---
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private val _loginState = MutableStateFlow(LoginState.DEFAULT)
    val loginState: StateFlow<LoginState> = _loginState

    fun updateEmail(email: String) {
        _loginUiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _loginUiState.update { it.copy(password = password) }
    }

    fun togglePasswordVisibility() {
        _loginUiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun login(context: Context) {
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        _loginUiState.update { it.copy(isLoading = true, errorMessage = "") }

        viewModelScope.launch {
            Log.d("LoginDebug", "Trying login for $email")
            val uid = userRepository.login(email, password)
            if (uid != null) {
                Log.d("LoginDebug", "Login success for UID: $uid")
                UserSessionManager.saveUserUid(context, uid)
                UserSessionManager.saveLoginTimestamp(context)

                _loginUiState.update {
                    it.copy(isLoading = false, errorMessage = "")
                }
                _loginState.value = LoginState.SUCCESS
            } else {
                Log.d("LoginDebug", "Login failed: Incorrect email or password")
                _loginUiState.update {
                    it.copy(isLoading = false, errorMessage = "Email or password is incorrect")
                }
                _loginState.value = LoginState.FAILURE
            }
        }
    }



    fun loginWithGoogle(idToken: String, context: Context, onRegisterNeeded: (String) -> Unit) {
        _loginUiState.update { it.copy(isLoading = true) }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                viewModelScope.launch {
                    val exists = userRepository.getUserByEmail(email)
                    if (exists) {
                        UserSessionManager.saveUserUid(context, uid)
                        UserSessionManager.saveLoginTimestamp(context)
                        _loginState.value = LoginState.SUCCESS
                        _loginUiState.update { it.copy(isLoading = false) }
                    } else {
                        _loginUiState.update { it.copy(isLoading = false) }
                        _loginState.value = LoginState.DEFAULT
                        onRegisterNeeded(email)
                    }
                }
            }
            .addOnFailureListener {
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Google login failed"
                    )
                }
                _loginState.value = LoginState.FAILURE
            }
    }

}
