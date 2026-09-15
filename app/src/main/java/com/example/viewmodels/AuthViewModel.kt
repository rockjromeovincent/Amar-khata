package com.example.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.localization.LocaleStrings
import com.example.models.AuthResult
import com.example.models.UserProfile
import com.example.repositories.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

data class PhoneAuthUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val verificationId: String? = null,
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val detectedOperator: String? = null,
    val resendCountdown: Int = 0,
    val canResend: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _phoneAuthState = MutableStateFlow(PhoneAuthUiState())
    val phoneAuthState: StateFlow<PhoneAuthUiState> = _phoneAuthState.asStateFlow()

    private var countdownJob: Job? = null

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
        _phoneAuthState.value = _phoneAuthState.value.copy(errorMessage = null, successMessage = null)
    }

    fun onPhoneNumberChanged(rawPhone: String) {
        val clean = LocaleStrings.normalizePhoneNumber(rawPhone)
        val operator = LocaleStrings.detectBangladeshiOperator(clean)
        _phoneAuthState.value = _phoneAuthState.value.copy(
            phoneNumber = clean,
            detectedOperator = operator,
            errorMessage = null
        )
    }

    fun onOtpCodeChanged(rawOtp: String) {
        val englishOtp = LocaleStrings.toEnglishDigits(rawOtp).replace("[^0-9]".toRegex(), "").take(6)
        _phoneAuthState.value = _phoneAuthState.value.copy(
            otpCode = englishOtp,
            errorMessage = null
        )
    }

    fun sendPhoneOtp(activity: Activity?, onSuccess: () -> Unit = {}) {
        val phone = _phoneAuthState.value.phoneNumber
        if (phone.isBlank()) {
            _phoneAuthState.value = _phoneAuthState.value.copy(
                errorMessage = LocaleStrings.ERROR_PHONE_EMPTY
            )
            return
        }

        if (!LocaleStrings.isValidBangladeshiPhone(phone)) {
            _phoneAuthState.value = _phoneAuthState.value.copy(
                errorMessage = LocaleStrings.ERROR_PHONE_INVALID_FORMAT
            )
            return
        }

        _phoneAuthState.value = _phoneAuthState.value.copy(isLoading = true, errorMessage = null)

        authRepository.sendPhoneOtp(
            activity = activity,
            phoneNumber = phone,
            onCodeSent = { verificationId ->
                _phoneAuthState.value = _phoneAuthState.value.copy(
                    isLoading = false,
                    isOtpSent = true,
                    verificationId = verificationId,
                    successMessage = LocaleStrings.SUCCESS_OTP_SENT,
                    errorMessage = null
                )
                startCountdown()
                onSuccess()
            },
            onAutoVerified = { userProfile ->
                _phoneAuthState.value = _phoneAuthState.value.copy(
                    isLoading = false,
                    isVerified = true,
                    successMessage = LocaleStrings.SUCCESS_PHONE_VERIFIED
                )
                _uiState.value = _uiState.value.copy(
                    isSuccess = true,
                    currentUser = userProfile,
                    successMessage = LocaleStrings.SUCCESS_LOGIN
                )
                onSuccess()
            },
            onError = { error ->
                _phoneAuthState.value = _phoneAuthState.value.copy(
                    isLoading = false,
                    errorMessage = error
                )
            }
        )
    }

    fun resendPhoneOtp(activity: Activity?) {
        if (!_phoneAuthState.value.canResend && _phoneAuthState.value.resendCountdown > 0) return
        sendPhoneOtp(activity)
    }

    fun verifyPhoneOtp(onSuccess: () -> Unit) {
        val state = _phoneAuthState.value
        val phone = state.phoneNumber
        val otp = state.otpCode
        val vId = state.verificationId ?: "sim_vid_$phone"

        if (otp.isBlank()) {
            _phoneAuthState.value = _phoneAuthState.value.copy(
                errorMessage = LocaleStrings.ERROR_OTP_EMPTY
            )
            return
        }

        if (otp.length != 6) {
            _phoneAuthState.value = _phoneAuthState.value.copy(
                errorMessage = LocaleStrings.ERROR_OTP_INVALID_LENGTH
            )
            return
        }

        viewModelScope.launch {
            _phoneAuthState.value = _phoneAuthState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.verifyPhoneOtp(phone, vId, otp)) {
                is AuthResult.Success -> {
                    _phoneAuthState.value = _phoneAuthState.value.copy(
                        isLoading = false,
                        isVerified = true,
                        successMessage = LocaleStrings.SUCCESS_PHONE_VERIFIED
                    )
                    _uiState.value = _uiState.value.copy(
                        isSuccess = true,
                        currentUser = result.data,
                        successMessage = LocaleStrings.SUCCESS_LOGIN
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _phoneAuthState.value = _phoneAuthState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                AuthResult.Loading -> {
                    _phoneAuthState.value = _phoneAuthState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun resetPhoneAuth() {
        countdownJob?.cancel()
        _phoneAuthState.value = PhoneAuthUiState()
    }

    fun fillTestOtp() {
        _phoneAuthState.value = _phoneAuthState.value.copy(
            otpCode = "123456",
            errorMessage = null
        )
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _phoneAuthState.value = _phoneAuthState.value.copy(resendCountdown = 60, canResend = false)
        countdownJob = viewModelScope.launch {
            for (i in 59 downTo 0) {
                delay(1000)
                _phoneAuthState.value = _phoneAuthState.value.copy(
                    resendCountdown = i,
                    canResend = (i == 0)
                )
            }
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}
