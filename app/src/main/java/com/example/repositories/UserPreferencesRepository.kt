package com.example.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.models.AppLanguage
import com.example.models.AppThemeMode
import com.example.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "amar_khata_prefs")

open class UserPreferencesRepository(private val context: Context? = null) {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_UID = stringPreferencesKey("user_uid")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_SHOP_NAME = stringPreferencesKey("user_shop_name")
        val USER_DISTRICT = stringPreferencesKey("user_district")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE_MODE = stringPreferencesKey("language_mode")
    }

    private val inMemoryOnboarding = MutableStateFlow(false)
    private val inMemoryIsLoggedIn = MutableStateFlow(false)
    private val inMemoryProfile = MutableStateFlow(
        UserProfile(
            uid = "",
            name = "মোঃ রফিকুল ইসলাম",
            phone = "01712345678",
            email = "rafiq@example.com",
            shopName = "মেসার্স রফিক অ্যান্ড সন্স",
            district = "ঢাকা"
        )
    )
    private val inMemoryTheme = MutableStateFlow(AppThemeMode.SYSTEM)
    private val inMemoryLanguage = MutableStateFlow(AppLanguage.BANGLA)

    open val onboardingCompletedFlow: Flow<Boolean> = if (context != null) {
        context.dataStore.data.map { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] ?: false
        }
    } else {
        inMemoryOnboarding.asStateFlow()
    }

    open val isLoggedInFlow: Flow<Boolean> = if (context != null) {
        context.dataStore.data.map { preferences ->
            preferences[Keys.IS_LOGGED_IN] ?: false
        }
    } else {
        inMemoryIsLoggedIn.asStateFlow()
    }

    open val userProfileFlow: Flow<UserProfile> = if (context != null) {
        context.dataStore.data.map { preferences ->
            UserProfile(
                uid = preferences[Keys.USER_UID] ?: "",
                name = preferences[Keys.USER_NAME] ?: "মোঃ রফিকুল ইসলাম",
                phone = preferences[Keys.USER_PHONE] ?: "01712345678",
                email = preferences[Keys.USER_EMAIL] ?: "rafiq@example.com",
                shopName = preferences[Keys.USER_SHOP_NAME] ?: "মেসার্স রফিক অ্যান্ড সন্স",
                district = preferences[Keys.USER_DISTRICT] ?: "ঢাকা"
            )
        }
    } else {
        inMemoryProfile.asStateFlow()
    }

    open val themeModeFlow: Flow<AppThemeMode> = if (context != null) {
        context.dataStore.data.map { preferences ->
            when (preferences[Keys.THEME_MODE]) {
                AppThemeMode.LIGHT.name -> AppThemeMode.LIGHT
                AppThemeMode.DARK.name -> AppThemeMode.DARK
                else -> AppThemeMode.SYSTEM
            }
        }
    } else {
        inMemoryTheme.asStateFlow()
    }

    open val languageModeFlow: Flow<AppLanguage> = if (context != null) {
        context.dataStore.data.map { preferences ->
            when (preferences[Keys.LANGUAGE_MODE]) {
                AppLanguage.ENGLISH.name -> AppLanguage.ENGLISH
                else -> AppLanguage.BANGLA
            }
        }
    } else {
        inMemoryLanguage.asStateFlow()
    }

    open suspend fun setOnboardingCompleted(completed: Boolean) {
        if (context != null) {
            context.dataStore.edit { preferences ->
                preferences[Keys.ONBOARDING_COMPLETED] = completed
            }
        } else {
            inMemoryOnboarding.value = completed
        }
    }

    open suspend fun saveUserSession(profile: UserProfile) {
        if (context != null) {
            context.dataStore.edit { preferences ->
                preferences[Keys.IS_LOGGED_IN] = true
                preferences[Keys.USER_UID] = profile.uid
                preferences[Keys.USER_NAME] = profile.name
                preferences[Keys.USER_PHONE] = profile.phone
                preferences[Keys.USER_EMAIL] = profile.email
                preferences[Keys.USER_SHOP_NAME] = profile.shopName
                preferences[Keys.USER_DISTRICT] = profile.district
            }
        } else {
            inMemoryIsLoggedIn.value = true
            inMemoryProfile.value = profile
        }
    }

    open suspend fun clearUserSession() {
        if (context != null) {
            context.dataStore.edit { preferences ->
                preferences[Keys.IS_LOGGED_IN] = false
                preferences.remove(Keys.USER_UID)
            }
        } else {
            inMemoryIsLoggedIn.value = false
            inMemoryProfile.value = UserProfile()
        }
    }

    open suspend fun setThemeMode(themeMode: AppThemeMode) {
        if (context != null) {
            context.dataStore.edit { preferences ->
                preferences[Keys.THEME_MODE] = themeMode.name
            }
        } else {
            inMemoryTheme.value = themeMode
        }
    }

    open suspend fun setLanguage(language: AppLanguage) {
        if (context != null) {
            context.dataStore.edit { preferences ->
                preferences[Keys.LANGUAGE_MODE] = language.name
            }
        } else {
            inMemoryLanguage.value = language
        }
    }

    open suspend fun updateShopProfile(shopName: String, district: String) {
        if (context != null) {
            context.dataStore.edit { preferences ->
                preferences[Keys.USER_SHOP_NAME] = shopName
                preferences[Keys.USER_DISTRICT] = district
            }
        } else {
            val current = inMemoryProfile.value
            inMemoryProfile.value = current.copy(shopName = shopName, district = district)
        }
    }
}

