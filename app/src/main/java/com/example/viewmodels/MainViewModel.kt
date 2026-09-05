package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.database.dao.UserDao
import com.example.models.AppLanguage
import com.example.models.AppThemeMode
import com.example.models.UserProfile
import com.example.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val userDao: UserDao? = null
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = preferencesRepository.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.SYSTEM)

    val languageMode: StateFlow<AppLanguage> = preferencesRepository.languageModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.BANGLA)

    val userProfile: StateFlow<UserProfile> = preferencesRepository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val isLoggedIn: StateFlow<Boolean> = preferencesRepository.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isOnboardingCompleted: StateFlow<Boolean> = preferencesRepository.onboardingCompletedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(language)
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
        }
    }

    fun updateShopProfile(shopName: String, district: String) {
        viewModelScope.launch {
            preferencesRepository.updateShopProfile(shopName, district)
            val currentUid = userProfile.value.uid
            if (!currentUid.isNullOrBlank() && userDao != null) {
                val dbUser = userDao.getUserById(currentUid)
                if (dbUser != null) {
                    userDao.updateUser(dbUser.copy(shopName = shopName, district = district))
                }
            }
        }
    }

    class Factory(
        private val repository: UserPreferencesRepository,
        private val userDao: UserDao? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository, userDao) as T
        }
    }
}
