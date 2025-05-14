package com.example.monashswap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashswap.data.repository.UserRepository
import com.example.monashswap.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState.DEFAULT)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.DEFAULT
            val result = userRepository.login(email, password)
            _loginState.value = if (result) LoginState.SUCCESS else LoginState.FAILURE
        }
    }
}
