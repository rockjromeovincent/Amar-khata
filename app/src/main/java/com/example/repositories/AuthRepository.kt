package com.example.repositories

import com.example.core.database.dao.UserDao
import com.example.core.database.entities.UserEntity
import com.example.core.localization.LocaleStrings
import com.example.models.AuthResult
import com.example.models.UserProfile
import com.example.services.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
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

    suspend fun sendPasswordReset(identifier: String): AuthResult<Boolean> {
        return firebaseAuthService.sendPasswordReset(identifier)
    }

    suspend fun logout() {
        firebaseAuthService.logout()
        userPreferencesRepository.clearUserSession()
    }
}

