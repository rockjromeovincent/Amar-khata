package com.example.repositories

import android.app.Activity
import com.example.core.database.dao.UserDao
import com.example.core.database.entities.UserEntity
import com.example.core.localization.LocaleStrings
import com.example.models.AuthResult
import com.example.models.UserProfile
import com.example.services.FirebaseAuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthRepository(
    private val firebaseAuthService: FirebaseAuthService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userDao: UserDao
) {

    val isLoggedInFlow: Flow<Boolean> = userPreferencesRepository.isLoggedInFlow
    val userProfileFlow: Flow<UserProfile> = userPreferencesRepository.userProfileFlow
    val allRegisteredUsersFlow: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun login(identifier: String, pass: String): AuthResult<UserProfile> {
        val trimmedIdentifier = identifier.trim()
        
        // 1. Check local Room database first
        val localUser = userDao.getUserByIdentifier(trimmedIdentifier)
        if (localUser != null) {
            if (localUser.password.isBlank() || localUser.password == pass) {
                userDao.updateUser(localUser.copy(lastLoginAt = System.currentTimeMillis()))
                val profile = localUser.toUserProfile()
                userPreferencesRepository.saveUserSession(profile)
                return AuthResult.Success(profile)
            } else {
                return AuthResult.Error(LocaleStrings.ERROR_INVALID_CREDENTIALS)
            }
        }

        // 2. Check via Firebase authentication service
        val result = firebaseAuthService.loginWithEmailOrPhone(trimmedIdentifier, pass)
        if (result is AuthResult.Success) {
            val userEntity = UserEntity(
                uid = result.data.uid.ifBlank { UUID.randomUUID().toString() },
                name = result.data.name,
                phone = result.data.phone.ifBlank { if (!trimmedIdentifier.contains("@")) trimmedIdentifier else "" },
                email = result.data.email.ifBlank { if (trimmedIdentifier.contains("@")) trimmedIdentifier else "" },
                shopName = result.data.shopName,
                district = result.data.district,
                password = pass,
                registeredAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis(),
                isActive = true
            )
            userDao.insertUser(userEntity)
            userPreferencesRepository.saveUserSession(userEntity.toUserProfile())
            return AuthResult.Success(userEntity.toUserProfile())
        }
        return result
    }

    suspend fun register(
        name: String,
        phone: String,
        email: String,
        shopName: String,
        district: String,
        pass: String
    ): AuthResult<UserProfile> {
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim()

        // Check if phone or email already registered in local database
        val existingPhone = if (cleanPhone.isNotBlank()) userDao.getUserByPhone(cleanPhone) else null
        if (existingPhone != null) {
            return AuthResult.Error("এই মোবাইল নম্বর দিয়ে ইতিমধ্যেই একটি অ্যাকাউন্ট রয়েছে। লগইন করুন।")
        }

        val existingEmail = if (cleanEmail.isNotBlank()) userDao.getUserByEmail(cleanEmail) else null
        if (existingEmail != null) {
            return AuthResult.Error("এই ইমেইল দিয়ে ইতিমধ্যেই একটি অ্যাকাউন্ট রয়েছে। লগইন করুন।")
        }

        // Register in Firebase Auth (if available)
        val firebaseResult = firebaseAuthService.registerUser(
            name = name.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            shopName = shopName.trim(),
            district = district.trim(),
            pass = pass
        )

        val uid = (firebaseResult as? AuthResult.Success)?.data?.uid ?: UUID.randomUUID().toString()

        // ALWAYS SAVE COMPLETE USER REGISTRATION DATA INTO ROOM DATABASE
        val userEntity = UserEntity(
            uid = uid,
            name = name.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            shopName = shopName.trim(),
            district = district.trim(),
            password = pass,
            registeredAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis(),
            isActive = true
        )
        userDao.insertUser(userEntity)

        val profile = userEntity.toUserProfile()
        userPreferencesRepository.saveUserSession(profile)
        return AuthResult.Success(profile)
    }

    suspend fun quickDemoLogin(): AuthResult<UserProfile> {
        val demoEntity = UserEntity(
            uid = "demo_user_12345",
            name = "মোঃ রফিকুল ইসলাম",
            phone = "01711223344",
            email = "demo@amarkhata.com",
            shopName = "ভাই ভাই জেনারেল স্টোর",
            district = "ঢাকা",
            password = "demo_password",
            registeredAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis(),
            isActive = true
        )
        // Ensure demo user is persisted in database as well
        userDao.insertUser(demoEntity)

        val demoProfile = demoEntity.toUserProfile()
        userPreferencesRepository.saveUserSession(demoProfile)
        return AuthResult.Success(demoProfile)
    }

    suspend fun updateShopProfile(uid: String, shopName: String, district: String) {
        val user = userDao.getUserById(uid)
        if (user != null) {
            userDao.updateUser(user.copy(shopName = shopName, district = district))
        }
        userPreferencesRepository.updateShopProfile(shopName, district)
    }

    fun sendPhoneOtp(
        activity: Activity?,
        phoneNumber: String,
        onCodeSent: (String) -> Unit,
        onAutoVerified: suspend (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanPhone = LocaleStrings.normalizePhoneNumber(phoneNumber)
        firebaseAuthService.sendPhoneVerification(
            activity = activity,
            phoneNumber = cleanPhone,
            onCodeSent = onCodeSent,
            onAutoVerified = { profile ->
                // Handled in coroutine scope by caller or persistence
                val userEntity = UserEntity(
                    uid = profile.uid.ifBlank { UUID.randomUUID().toString() },
                    name = profile.name.ifBlank { "ব্যবসায়ী" },
                    phone = cleanPhone,
                    email = profile.email,
                    shopName = profile.shopName.ifBlank { "আমার ব্যবসা" },
                    district = profile.district.ifBlank { "ঢাকা" },
                    password = "",
                    registeredAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis(),
                    isActive = true
                )
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val existing = userDao.getUserByPhone(cleanPhone)
                    if (existing != null) {
                        userDao.updateUser(existing.copy(lastLoginAt = System.currentTimeMillis()))
                        userPreferencesRepository.saveUserSession(existing.toUserProfile())
                        onAutoVerified(existing.toUserProfile())
                    } else {
                        userDao.insertUser(userEntity)
                        userPreferencesRepository.saveUserSession(userEntity.toUserProfile())
                        onAutoVerified(userEntity.toUserProfile())
                    }
                }
            },
            onError = onError
        )
    }

    suspend fun verifyPhoneOtp(
        phoneNumber: String,
        verificationId: String,
        otpCode: String
    ): AuthResult<UserProfile> {
        val cleanPhone = LocaleStrings.normalizePhoneNumber(phoneNumber)
        val result = firebaseAuthService.verifyPhoneOtp(verificationId, otpCode, cleanPhone)
        
        if (result is AuthResult.Success) {
            val existing = userDao.getUserByPhone(cleanPhone)
            return if (existing != null) {
                userDao.updateUser(existing.copy(lastLoginAt = System.currentTimeMillis()))
                val profile = existing.toUserProfile()
                userPreferencesRepository.saveUserSession(profile)
                AuthResult.Success(profile)
            } else {
                val userEntity = UserEntity(
                    uid = result.data.uid.ifBlank { UUID.randomUUID().toString() },
                    name = result.data.name.ifBlank { "ব্যবসায়ী" },
                    phone = cleanPhone,
                    email = result.data.email,
                    shopName = result.data.shopName.ifBlank { "আমার ব্যবসা" },
                    district = result.data.district.ifBlank { "ঢাকা" },
                    password = "",
                    registeredAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis(),
                    isActive = true
                )
                userDao.insertUser(userEntity)
                val profile = userEntity.toUserProfile()
                userPreferencesRepository.saveUserSession(profile)
                AuthResult.Success(profile)
            }
        }
        return result
    }

    suspend fun sendPasswordReset(identifier: String): AuthResult<Boolean> {
        return firebaseAuthService.sendPasswordReset(identifier)
    }

    suspend fun logout() {
        firebaseAuthService.logout()
        userPreferencesRepository.clearUserSession()
    }
}

