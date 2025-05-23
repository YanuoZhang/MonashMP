package com.example.monashMP.model

import android.graphics.Bitmap
import com.example.monashMP.utils.Constants

data class RegisterUiState(
    val email: String = "",
    val nickname: String = "",
    val nicknameError: String = "",
    val birthday: String = "",
    val primaryCampus: String? = null,
    val campusOptions: List<String> = Constants.LOCATIONS,
    val password: String = "",
    val passwordError: String = "",
    val confirmPassword: String = "",
    val confirmPasswordError: String = "",
    val avatarBitmap: Bitmap? = null,
    val avatarBase64: String? = null,
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val isSubmitEnabled: Boolean = false
)
