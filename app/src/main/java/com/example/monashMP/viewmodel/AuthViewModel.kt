package com.example.monashMP.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.model.LoginState
import com.example.monashMP.data.model.LoginUiState
import com.example.monashMP.data.model.RegisterUiState
import com.example.monashMP.data.model.UserModel
import com.example.monashMP.data.model.toMap
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.calculateSubmitEnabled
import com.example.monashMP.utils.isValidPassword
import com.example.monashMP.utils.md5
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

    // --- Registration State ---

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

    /**
     * Submits registration info to Firebase if all fields are valid.
     * If avatar is provided, it will be uploaded to Firebase Storage.
     * Then user data will be saved to Firebase Realtime DB.
     */
    fun submit(context: Context, onSuccess: () -> Unit) {
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
                        password = current.password.md5(),
                        avatarUrl = avatarUrl,
                        nickname = current.nickname,
                        birthday = current.birthday,
                        primaryCampus = current.primaryCampus,
                        createdAt = System.currentTimeMillis()
                    )

                    userRepository.registerUser(uid, user.toMap())

                    // Save session after successful registration
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

}
