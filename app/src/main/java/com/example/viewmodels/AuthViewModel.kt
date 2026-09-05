package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.localization.LocaleStrings
import com.example.models.AuthResult
import com.example.models.UserProfile
import com.example.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentUser: UserProfile? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(identifier: String, pass: String, onSuccess: () -> Unit) {
        if (identifier.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = LocaleStrings.ERROR_REQUIRED_FIELD
            )
            return
        }

        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = LocaleStrings.ERROR_PASSWORD_LENGTH
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.login(identifier, pass)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        currentUser = result.data,
                        successMessage = LocaleStrings.SUCCESS_LOGIN
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun register(
        name: String,
        phone: String,
        email: String,
        shopName: String,
        district: String,
        pass: String,
        confirmPass: String,
        termsAccepted: Boolean,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || phone.isBlank() || shopName.isBlank() || district.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = LocaleStrings.ERROR_INVALID_INPUT
            )
            return
        }

        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = LocaleStrings.ERROR_PASSWORD_LENGTH
            )
            return
        }

        if (pass != confirmPass) {
            _uiState.value = _uiState.value.copy(
                errorMessage = LocaleStrings.ERROR_PASSWORD_MISMATCH
            )
            return
        }

        if (!termsAccepted) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "রেজিস্ট্রেশনের জন্য সেবা শর্তাবলী মেনে নিন"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.register(
                name = name,
                phone = phone,
                email = email,
                shopName = shopName,
                district = district,
                pass = pass
            )) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        currentUser = result.data,
                        successMessage = LocaleStrings.SUCCESS_REGISTER
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun quickDemoLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.quickDemoLogin()
            if (result is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    currentUser = result.data,
                    successMessage = "ডেমো অ্যাকাউন্টে প্রবেশ করা হয়েছে"
                )
                onSuccess()
            }
        }
    }

    fun sendPasswordReset(identifier: String, onSuccess: () -> Unit) {
        if (identifier.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "অনুগ্রহ করে আপনার মোবাইল নম্বর অথবা ইমেইল দিন"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            authRepository.sendPasswordReset(identifier)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                successMessage = LocaleStrings.RESET_LINK_SENT
            )
            onSuccess()
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
            onLogoutComplete()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}
