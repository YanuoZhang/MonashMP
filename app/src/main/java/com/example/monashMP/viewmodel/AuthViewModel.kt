package com.example.monashMP.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.model.LoginState
import com.example.monashMP.model.LoginUiState
import com.example.monashMP.model.RegisterUiState
import com.example.monashMP.model.UserModel
import com.example.monashMP.model.toMap
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.isValidPassword
import com.example.monashMP.utils.md5
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for handling user login and registration logic.
 * Supports email/password login, Google login, registration, and session management.
 */
class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // -------------------------
    // Login State Management
    // -------------------------

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

    /**
     * Attempts email/password login and updates session state.
     */
    fun login(context: Context) {
        val (email, password) = _loginUiState.value
        _loginUiState.update { it.copy(isLoading = true, errorMessage = "") }

        viewModelScope.launch {
            val uid = userRepository.login(email, password)
            if (uid != null) {
                UserSessionManager.saveUserUid(context, uid)
                UserSessionManager.saveLoginTimestamp(context)
                _loginUiState.update { it.copy(isLoading = false) }
                _loginState.value = LoginState.SUCCESS
            } else {
                _loginUiState.update {
                    it.copy(isLoading = false, errorMessage = "Email or password is incorrect")
                }
                _loginState.value = LoginState.FAILURE
            }
        }
    }

    /**
     * Handles Google login. If user doesn't exist, triggers registration flow.
     */
    fun loginWithGoogle(idToken: String, context: Context, onRegisterNeeded: (String) -> Unit) {
        _loginUiState.update { it.copy(isLoading = true) }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                val email = it.user?.email ?: ""
                val uid = it.user?.uid ?: ""
                viewModelScope.launch {
                    if (userRepository.getUserByEmail(email)) {
                        UserSessionManager.saveUserUid(context, uid)
                        UserSessionManager.saveLoginTimestamp(context)
                        _loginState.value = LoginState.SUCCESS
                    } else {
                        onRegisterNeeded(email)
                        _loginState.value = LoginState.DEFAULT
                    }
                    _loginUiState.update { it.copy(isLoading = false) }
                }
            }
            .addOnFailureListener {
                _loginUiState.update { it.copy(isLoading = false, errorMessage = "Google login failed") }
                _loginState.value = LoginState.FAILURE
            }
    }

    /**
     * Clears login session.
     */
    fun logout(context: Context) {
        _loginState.value = LoginState.DEFAULT
        viewModelScope.launch {
            UserSessionManager.clearSession(context)
        }
    }

    // -------------------------
    // Registration State
    // -------------------------

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState

    fun setRegisterEmail(email: String) = _registerState.update { it.copy(email = email) }
    fun onNicknameChanged(nickname: String) = _registerState.updateAndValidate {
        it.copy(nickname = nickname, nicknameError = if (nickname.isBlank()) "Nickname is required" else "")
    }

    fun onBirthdayChanged(birthday: String) = _registerState.updateAndValidate { it.copy(birthday = birthday) }
    fun onCampusChanged(campus: String) = _registerState.updateAndValidate { it.copy(primaryCampus = campus) }

    fun onPasswordChanged(password: String) = _registerState.updateAndValidate {
        it.copy(
            password = password,
            passwordError = if (!password.isValidPassword()) "Must contain letters, digits, special chars, min 8" else ""
        )
    }

    fun onConfirmPasswordChanged(confirmPassword: String) = _registerState.updateAndValidate {
        it.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = if (confirmPassword != it.password) "Passwords do not match" else ""
        )
    }

    fun togglePasswordVisibilityRegister() = _registerState.updateAndValidate {
        it.copy(showPassword = !it.showPassword)
    }

    fun toggleConfirmPasswordVisibility() = _registerState.updateAndValidate {
        it.copy(showConfirmPassword = !it.showConfirmPassword)
    }

    fun setAvatar(bitmap: Bitmap) = _registerState.updateAndValidate {
        it.copy(avatarBitmap = bitmap)
    }

    /**
     * Submits registration to Firebase and stores user in Realtime DB.
     */
    fun submit(context: Context, onSuccess: () -> Unit) {
        val state = _registerState.value
        if (!state.isSubmitEnabled) return

        _registerState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                if (!userRepository.getUserByEmail(state.email)) {
                    val avatarUrl = state.avatarBitmap?.let { userRepository.uploadAvatarToFirebase(uid, it) } ?: ""
                    val user = UserModel(
                        uid = uid,
                        email = state.email,
                        password = state.password.md5(),
                        avatarUrl = avatarUrl,
                        nickname = state.nickname,
                        birthday = state.birthday,
                        primaryCampus = state.primaryCampus ?: "Unknown",
                        createdAt = System.currentTimeMillis()
                    )
                    userRepository.registerUser(uid, user.toMap())
                    UserSessionManager.saveUserUid(context, uid)
                    UserSessionManager.saveLoginTimestamp(context)
                    _registerState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    onSuccess()
                } else {
                    _registerState.update { it.copy(isSubmitting = false) }
                }
            } catch (e: Exception) {
                Log.e("Register", "Submit failed", e)
                _registerState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private fun calculateSubmitEnabled(state: RegisterUiState): Boolean {
        return state.nickname.isNotBlank()
                && state.password.isValidPassword()
                && state.password == state.confirmPassword
                && !state.primaryCampus.isNullOrBlank()
    }

    // Extension to help reduce repetition
    private fun MutableStateFlow<RegisterUiState>.updateAndValidate(transform: (RegisterUiState) -> RegisterUiState) {
        this.update {
            val updated = transform(it)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }
}
