package com.example

import com.example.core.database.dao.UserDao
import com.example.core.database.entities.UserEntity
import com.example.core.localization.LocaleStrings
import com.example.models.AuthResult
import com.example.models.UserProfile
import com.example.repositories.AuthRepository
import com.example.repositories.UserPreferencesRepository
import com.example.services.FirebaseAuthService
import com.example.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PhoneAuthFlowTest {

    private lateinit var fakeUserDao: FakePhoneTestUserDao
    private lateinit var testPrefs: UserPreferencesRepository
    private lateinit var fakeAuthService: FakePhoneFirebaseAuthService
    private lateinit var authRepo: AuthRepository
    private lateinit var authViewModel: AuthViewModel

    class FakePhoneTestUserDao : UserDao {
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
                it.phone == identifier || it.email.equals(identifier, ignoreCase = true)
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

    class FakePhoneFirebaseAuthService : FirebaseAuthService() {
        override suspend fun verifyPhoneOtp(
            verificationId: String,
            otpCode: String,
            phoneNumber: String
        ): AuthResult<UserProfile> {
            return if (otpCode == "123456" || otpCode == "654321") {
                AuthResult.Success(
                    UserProfile(
                        uid = "uid_phone_${phoneNumber}",
                        name = "ব্যবসায়ী",
                        phone = phoneNumber,
                        email = "",
                        shopName = "আমার ব্যবসা",
                        district = "ঢাকা"
                    )
                )
            } else {
                AuthResult.Error(LocaleStrings.ERROR_OTP_INVALID)
            }
        }
    }

    @Before
    fun setUp() {
        fakeUserDao = FakePhoneTestUserDao()
        testPrefs = UserPreferencesRepository()
        fakeAuthService = FakePhoneFirebaseAuthService()
        authRepo = AuthRepository(fakeAuthService, testPrefs, fakeUserDao)
        authViewModel = AuthViewModel(authRepo)
    }

    @Test
    fun testBanglaDigitConversionAndNormalization() {
        // Test Bengali numeral to Western numeral conversion
        val banglaInput = "০১৭৮৮২৩৯৮৫৯"
        val converted = LocaleStrings.toEnglishDigits(banglaInput)
        assertEquals("01788239859", converted)

        // Test Western numeral to Bengali numeral
        val westernInput = "01788239859"
        val banglaFormatted = LocaleStrings.toBanglaDigits(westernInput)
        assertEquals("০১৭৮৮২৩৯৮৫৯", banglaFormatted)

        // Test Phone Normalization with BD prefix and dashes
        assertEquals("01712345678", LocaleStrings.normalizePhoneNumber("+8801712345678"))
        assertEquals("01712345678", LocaleStrings.normalizePhoneNumber("8801712345678"))
        assertEquals("01712345678", LocaleStrings.normalizePhoneNumber("01712-345678"))
        assertEquals("01712345678", LocaleStrings.normalizePhoneNumber("+৮৮০ ০১৭১২-৩৪৫৬৭৮"))
    }

    @Test
    fun testBangladeshiOperatorDetection() {
        // Grameenphone
        assertEquals(LocaleStrings.OPERATOR_GP, LocaleStrings.detectBangladeshiOperator("01712345678"))
        assertEquals(LocaleStrings.OPERATOR_GP, LocaleStrings.detectBangladeshiOperator("01312345678"))
        assertEquals(LocaleStrings.OPERATOR_GP, LocaleStrings.detectBangladeshiOperator("০১৭৮৮২৩৯৮৫৯"))

        // Banglalink
        assertEquals(LocaleStrings.OPERATOR_BL, LocaleStrings.detectBangladeshiOperator("01912345678"))
        assertEquals(LocaleStrings.OPERATOR_BL, LocaleStrings.detectBangladeshiOperator("01412345678"))

        // Robi
        assertEquals(LocaleStrings.OPERATOR_ROBI, LocaleStrings.detectBangladeshiOperator("01812345678"))

        // Airtel
        assertEquals(LocaleStrings.OPERATOR_AIRTEL, LocaleStrings.detectBangladeshiOperator("01612345678"))

        // Teletalk
        assertEquals(LocaleStrings.OPERATOR_TELETALK, LocaleStrings.detectBangladeshiOperator("01512345678"))
    }

    @Test
    fun testBangladeshiPhoneValidation() {
        // Valid 11 digit numbers
        assertTrue(LocaleStrings.isValidBangladeshiPhone("01712345678"))
        assertTrue(LocaleStrings.isValidBangladeshiPhone("+8801712345678"))
        assertTrue(LocaleStrings.isValidBangladeshiPhone("০১৭৮৮২৩৯৮৫৯"))

        // Invalid: short or wrong prefix
        assertFalse(LocaleStrings.isValidBangladeshiPhone("01212345678")) // prefix 012 is invalid
        assertFalse(LocaleStrings.isValidBangladeshiPhone("0171234567"))  // 10 digits
        assertFalse(LocaleStrings.isValidBangladeshiPhone("017123456789")) // 12 digits
        assertFalse(LocaleStrings.isValidBangladeshiPhone("abcdefghijk"))
    }

    @Test
    fun testPhoneAuthVerificationAndDatabasePersistence() = runBlocking {
        val phoneNumber = "01788239859"
        val otp = "123456"
        val verificationId = "sim_vid_test"

        // Verify Phone OTP
        val result = authRepo.verifyPhoneOtp(phoneNumber, verificationId, otp)
        assertTrue("Expected successful authentication", result is AuthResult.Success)

        val profile = (result as AuthResult.Success).data
        assertEquals(phoneNumber, profile.phone)

        // Verify user was stored in Room database
        val persistedUser = fakeUserDao.getUserByPhone(phoneNumber)
        assertNotNull("User must be stored in database", persistedUser)
        assertEquals(phoneNumber, persistedUser?.phone)
        assertEquals("ব্যবসায়ী", persistedUser?.name)

        // Subsequent OTP verification for existing user updates lastLoginAt
        val secondResult = authRepo.verifyPhoneOtp(phoneNumber, verificationId, otp)
        assertTrue(secondResult is AuthResult.Success)
        val updatedUser = fakeUserDao.getUserByPhone(phoneNumber)
        assertNotNull(updatedUser)
        assertEquals(persistedUser?.uid, updatedUser?.uid)
    }

    @Test
    fun testAuthViewModelPhoneStateHandling() {
        // Test Phone number input and operator detection in ViewModel
        authViewModel.onPhoneNumberChanged("০১৭৮৮২৩৯৮৫৯")
        val state = authViewModel.phoneAuthState.value
        assertEquals("01788239859", state.phoneNumber)
        assertEquals(LocaleStrings.OPERATOR_GP, state.detectedOperator)

        // Test OTP input formatting
        authViewModel.onOtpCodeChanged("১২৩৪৫৬")
        val otpState = authViewModel.phoneAuthState.value
        assertEquals("123456", otpState.otpCode)

        // Test filling test OTP
        authViewModel.fillTestOtp()
        assertEquals("123456", authViewModel.phoneAuthState.value.otpCode)

        // Test reset
        authViewModel.resetPhoneAuth()
        assertEquals("", authViewModel.phoneAuthState.value.phoneNumber)
        assertEquals("", authViewModel.phoneAuthState.value.otpCode)
    }
}
