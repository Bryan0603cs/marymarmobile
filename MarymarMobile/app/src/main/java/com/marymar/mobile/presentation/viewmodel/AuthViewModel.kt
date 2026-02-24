package com.marymar.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marymar.mobile.core.util.ApiResult
import com.marymar.mobile.domain.model.Role
import com.marymar.mobile.domain.usecase.LoginUseCase
import com.marymar.mobile.domain.usecase.RegisterUseCase
import com.marymar.mobile.domain.usecase.ValidateCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateCodeUseCase: ValidateCodeUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    fun login(email: String, password: String) {
        _ui.value = _ui.value.copy(loading = true, error = null, next = null)
        viewModelScope.launch {
            when (val res = loginUseCase(email, password)) {
                is ApiResult.Success -> {
                    val step = res.data
                    _ui.value = _ui.value.copy(
                        loading = false,
                        next = if (step.requires2FA) AuthNext.GoToCode(email) else AuthNext.LoggedIn
                    )
                }
                is ApiResult.Error -> _ui.value = _ui.value.copy(loading = false, error = res.message)
            }
        }
    }

    fun validateCode(email: String, code: String) {
        _ui.value = _ui.value.copy(loading = true, error = null, next = null)
        viewModelScope.launch {
            when (val res = validateCodeUseCase(email, code)) {
                is ApiResult.Success -> _ui.value = _ui.value.copy(loading = false, next = AuthNext.LoggedIn)
                is ApiResult.Error -> _ui.value = _ui.value.copy(loading = false, error = res.message)
            }
        }
    }

    fun register(
        idNumber: String,
        name: String,
        email: String,
        password: String,
        phone: String,
        birthDateIso: String,
        role: Role
    ) {
        _ui.value = _ui.value.copy(loading = true, error = null, next = null)
        viewModelScope.launch {
            when (
                val res = registerUseCase(
                    idNumber,
                    name,
                    email,
                    password,
                    phone,
                    birthDateIso,
                    role
                )
            ) {
                is ApiResult.Success -> _ui.value = _ui.value.copy(loading = false, next = AuthNext.LoggedIn)
                is ApiResult.Error -> _ui.value = _ui.value.copy(loading = false, error = res.message)
            }
        }
    }

    fun consumeNext() {
        _ui.value = _ui.value.copy(next = null)
    }
}

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val next: AuthNext? = null
)

sealed class AuthNext {
    data class GoToCode(val email: String) : AuthNext()
    data object LoggedIn : AuthNext()
}
