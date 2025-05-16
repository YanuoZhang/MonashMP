package com.example.monashMP.utils

import com.example.monashMP.model.RegisterUiState

fun calculateSubmitEnabled(state: RegisterUiState): Boolean {
    return state.nickname.isNotBlank() &&
            state.password.isValidPassword() &&
            state.password == state.confirmPassword &&
            state.primaryCampus != null
}