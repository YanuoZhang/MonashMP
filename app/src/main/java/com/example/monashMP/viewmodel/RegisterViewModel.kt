package com.example.monashMP.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.model.RegisterUiState
import com.example.monashMP.model.UserModel
import com.example.monashMP.model.toMap
import com.example.monashMP.utils.calculateSubmitEnabled
import com.example.monashMP.utils.isValidPassword
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNicknameChanged(nickname: String) {
        _uiState.update {
            val updated = it.copy(
                nickname = nickname,
                nicknameError = if (nickname.isBlank()) "Nickname is required" else ""
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onBirthdayChanged(birthday: String) {
         _uiState.update {
             val updated = it.copy(birthday = birthday)
             updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onCampusChanged(campus: String) {
        _uiState.update {
            val updated = it.copy(primaryCampus = campus)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            val updated = it.copy(
                password = password,
                passwordError = if (!password.isValidPassword()) "Must contain letters, digits, special chars, min 8" else ""
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update {
            val updated = it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword.isNotEmpty() && confirmPassword != it.password)
                    "Passwords do not match" else ""
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            val updated = it.copy(showPassword = !it.showPassword)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update {
            val updated = it.copy(showConfirmPassword = !it.showConfirmPassword)
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun setAvatar(bitmap: Bitmap) {
        _uiState.update {
            val updated = it.copy(
                avatarBitmap = bitmap
            )
            updated.copy(isSubmitEnabled = calculateSubmitEnabled(updated))
        }
    }

    fun submit(email: String, onSuccess: () -> Unit) {
        _uiState.update {
            it.copy(
                isSubmitting = true,
                email = email
            )
        }

        val current = _uiState.value

        if (
            current.nickname.isBlank() ||
            current.passwordError.isNotEmpty() ||
            current.confirmPasswordError.isNotEmpty() ||
            current.primaryCampus.isNullOrEmpty()
        ) {
            _uiState.update { it.copy(isSubmitting = false) }
            return
        }

        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    _uiState.update { it.copy(isSubmitting = false) }
                    return@launch
                }

                val exists = userRepository.getUserByEmail(email)
                if (!exists) {
                    val avatarBitmap = current.avatarBitmap
                    val avatarUrl = if (avatarBitmap != null) {
                        userRepository.uploadAvatarToFirebase(uid, avatarBitmap)
                    } else ""

                    val user = UserModel(
                        uid = uid,
                        email = email,
                        avatarUrl = avatarUrl,
                        nickName = current.nickname,
                        birthday = current.birthday,
                        primaryCampus = current.primaryCampus
                    )

                    val userMap = user.toMap()
                    userRepository.registerUser(uid, userMap)

                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isSubmitting = false) }
                }
            } catch (e: Exception) {
                Log.e("Register", "Submit failed", e)
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }


}
