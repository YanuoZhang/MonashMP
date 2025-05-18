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
import com.example.monashMP.utils.calculateSubmitEnabled
import com.example.monashMP.utils.isValidPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for both login and registration logic.
 */
class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // --- login ---
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private val _loginState = MutableStateFlow<LoginState>(LoginState.DEFAULT)
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
            val result = userRepository.login(email, password)
            if (result) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    UserSessionManager.saveUserUid(context, uid)
                    UserSessionManager.saveLoginTimestamp(context)
                }
                _loginUiState.update { it.copy(isLoading = false, loginState = LoginState.SUCCESS) }
            } else {
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        loginState = LoginState.FAILURE,
                        errorMessage = "Email or password is incorrect"
                    )
                }
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
                    } else {
                        _loginState.value = LoginState.DEFAULT
                        onRegisterNeeded(email)
                    }
                    _loginUiState.update { it.copy(isLoading = false) }
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

    // --- Registration ---

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState

    fun setRegisterEmail(email: String) {
        _registerState.update { it.copy(email = email) }
    }

    fun onNicknameChanged(nickname: String) {
        _registerState.update {
            val updated = it.copy(
                nickname = nickname,
                nicknameError = if (nickname.isBlank()) "Nickname is required" else ""
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onBirthdayChanged(birthday: String) {
        _registerState.update {
            val updated = it.copy(birthday = birthday)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onCampusChanged(campus: String) {
        _registerState.update {
            val updated = it.copy(primaryCampus = campus)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onPasswordChanged(password: String) {
        _registerState.update {
            val updated = it.copy(
                password = password,
                passwordError = if (!password.isValidPassword()) "Must contain letters, digits, special chars, min 8" else ""
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _registerState.update {
            val updated = it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword.isNotEmpty() && confirmPassword != it.password)
                    "Passwords do not match" else ""
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun togglePasswordVisibilityRegister() {
        _registerState.update {
            val updated = it.copy(showPassword = !it.showPassword)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun toggleConfirmPasswordVisibility() {
        _registerState.update {
            val updated = it.copy(showConfirmPassword = !it.showConfirmPassword)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun setAvatar(bitmap: Bitmap) {
        _registerState.update {
            val updated = it.copy(avatarBitmap = bitmap)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun submit(onSuccess: () -> Unit) {
        val current = _registerState.value
        _registerState.update { it.copy(isSubmitting = true) }

        if (
            current.email.isBlank() ||
            current.nickname.isBlank() ||
            current.passwordError.isNotEmpty() ||
            current.confirmPasswordError.isNotEmpty() ||
            current.primaryCampus.isNullOrEmpty()
        ) {
            _registerState.update { it.copy(isSubmitting = false) }
            return
        }

        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    _registerState.update { it.copy(isSubmitting = false) }
                    return@launch
                }

                val exists = userRepository.getUserByEmail(current.email)
                if (!exists) {
                    val avatarBitmap = current.avatarBitmap
                    val avatarUrl = if (avatarBitmap != null) {
                        userRepository.uploadAvatarToFirebase(uid, avatarBitmap)
                    } else ""

                    val user = UserModel(
                        uid = uid,
                        email = current.email,
                        avatarUrl = avatarUrl,
                        nickname = current.nickname,
                        birthday = current.birthday,
                        primaryCampus = current.primaryCampus
                    )

                    val userMap = user.toMap()
                    userRepository.registerUser(uid, userMap)

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
}
