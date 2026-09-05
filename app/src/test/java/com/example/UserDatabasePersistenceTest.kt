package com.example

import com.example.core.database.dao.UserDao
import com.example.core.database.entities.UserEntity
import com.example.models.AuthResult
import com.example.models.UserProfile
import com.example.repositories.AuthRepository
import com.example.repositories.UserPreferencesRepository
import com.example.services.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserDatabasePersistenceTest {

    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var testPrefs: UserPreferencesRepository
    private lateinit var authRepo: AuthRepository

    // In-memory fake implementation of UserDao to test database persistence contract
    class FakeUserDao : UserDao {
        val database = mutableMapOf<String, UserEntity>()

        override suspend fun insertUser(user: UserEntity) {
            database[user.uid] = user
        }

        override suspend fun updateUser(user: UserEntity) {
            database[user.uid] = user
        }

        override suspend fun getUserById(uid: String): UserEntity? {
            return database[uid]
        }

        override suspend fun getUserByPhone(phone: String): UserEntity? {
            return database.values.firstOrNull { it.phone == phone }
        }

        override suspend fun getUserByEmail(email: String): UserEntity? {
            return database.values.firstOrNull { it.email.equals(email, ignoreCase = true) }
        }

        override suspend fun getUserByIdentifier(identifier: String): UserEntity? {
            return database.values.firstOrNull {
                it.phone == identifier || it.email.equals(identifier, ignoreCase = true) || it.uid == identifier
            }
        }

        override fun getAllUsers(): Flow<List<UserEntity>> {
            return flowOf(database.values.toList())
        }

        override suspend fun getAllUsersList(): List<UserEntity> {
            return database.values.toList()
        }

        override suspend fun getLatestActiveUser(): UserEntity? {
            return database.values.maxByOrNull { it.lastLoginAt }
        }

        override suspend fun getUserCount(): Int {
            return database.size
        }

        override suspend fun deleteUser(uid: String) {
            database.remove(uid)
        }
    }

    class FakeFirebaseAuthService : FirebaseAuthService() {
        override suspend fun registerUser(
            name: String,
            phone: String,
            email: String,
            shopName: String,
            district: String,
            pass: String
        ): AuthResult<UserProfile> {
            return AuthResult.Success(
                UserProfile(
                    uid = "uid_" + System.currentTimeMillis(),
                    name = name,
                    phone = phone,
                    email = email,
                    shopName = shopName,
                    district = district
                )
            )
        }

        override suspend fun loginWithEmailOrPhone(
            identifier: String,
            pass: String
        ): AuthResult<UserProfile> {
            return AuthResult.Success(
                UserProfile(
                    uid = "uid_login",
                    name = "ব্যবসায়ী",
                    phone = if (!identifier.contains("@")) identifier else "",
                    email = if (identifier.contains("@")) identifier else "",
                    shopName = "আমার দোকান",
                    district = "ঢাকা"
                )
            )
        }
    }

    @Before
    fun setUp() {
        fakeUserDao = FakeUserDao()
        testPrefs = UserPreferencesRepository()
        authRepo = AuthRepository(
            firebaseAuthService = FakeFirebaseAuthService(),
            userPreferencesRepository = testPrefs,
            userDao = fakeUserDao
        )
    }

    @Test
    fun userRegistration_savesAllUserDataToDatabase() = runBlocking {
        assertEquals(0, fakeUserDao.getUserCount())

        val result = authRepo.register(
            name = "মোঃ তানভীর আহমেদ",
            phone = "01812345678",
            email = "tanvir@business.com",
            shopName = "তানভীর ইলেকট্রনিক্স",
            district = "চট্টগ্রাম",
            pass = "secretPass123"
        )

        assertTrue(result is AuthResult.Success)
        assertEquals(1, fakeUserDao.getUserCount())

        val savedUser = fakeUserDao.getUserByPhone("01812345678")
        assertNotNull(savedUser)
        assertEquals("মোঃ তানভীর আহমেদ", savedUser?.name)
        assertEquals("01812345678", savedUser?.phone)
        assertEquals("tanvir@business.com", savedUser?.email)
        assertEquals("তানভীর ইলেকট্রনিক্স", savedUser?.shopName)
        assertEquals("চট্টগ্রাম", savedUser?.district)
        assertEquals("secretPass123", savedUser?.password)
        assertTrue((savedUser?.registeredAt ?: 0) > 0)
    }

    @Test
    fun userLogin_authenticatesDirectlyFromDatabase() = runBlocking {
        // Register user in database
        authRepo.register(
            name = "আব্দুল করিম",
            phone = "01998877665",
            email = "karim@store.com",
            shopName = "করিম ট্রেডিং",
            district = "সিলেট",
            pass = "passKarim456"
        )

        // Login with phone and correct password
        val loginResult = authRepo.login("01998877665", "passKarim456")
        assertTrue(loginResult is AuthResult.Success)
        val profile = (loginResult as AuthResult.Success).data
        assertEquals("আব্দুল করিম", profile.name)
        assertEquals("করিম ট্রেডিং", profile.shopName)

        // Login with email and correct password
        val loginWithEmailResult = authRepo.login("karim@store.com", "passKarim456")
        assertTrue(loginWithEmailResult is AuthResult.Success)

        // Login with wrong password should fail
        val wrongPassResult = authRepo.login("01998877665", "wrongPassword")
        assertTrue(wrongPassResult is AuthResult.Error)
    }

    @Test
    fun duplicateRegistration_isBlockedByDatabase() = runBlocking {
        authRepo.register(
            name = "হাসান মাহমুদ",
            phone = "01555444333",
            email = "hasan@market.com",
            shopName = "হাসান বস্ত্রালয়",
            district = "বগুড়া",
            pass = "bogra123"
        )

        // Attempting duplicate phone
        val duplicateResult = authRepo.register(
            name = "অন্য নাম",
            phone = "01555444333",
            email = "other@market.com",
            shopName = "অন্য দোকান",
            district = "ঢাকা",
            pass = "pass1234"
        )
        assertTrue(duplicateResult is AuthResult.Error)
        assertEquals(1, fakeUserDao.getUserCount())
    }
}
