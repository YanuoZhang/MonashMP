package com.example.monashMP.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val loginState: LoginState = LoginState.DEFAULT
)