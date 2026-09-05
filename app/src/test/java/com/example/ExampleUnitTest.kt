package com.example

import com.example.core.localization.LocaleStrings
import com.example.models.BangladeshDistricts
import com.example.models.UserProfile
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun bangladeshDistricts_containsAll64Districts() {
        val districts = BangladeshDistricts.allDistricts
        assertEquals(64, districts.size)
        
        // Ensure no duplicate IDs
        val uniqueIds = districts.map { it.id }.toSet()
        assertEquals(64, uniqueIds.size)

        // Ensure Dhaka, Chattogram, Sylhet, Rajshahi, Khulna, Barishal, Rangpur, Mymensingh exist
        val divisions = districts.map { it.divisionBn }.toSet()
        assertTrue(divisions.contains("ঢাকা"))
        assertTrue(divisions.contains("চট্টগ্রাম"))
        assertTrue(divisions.contains("সিলেট"))
        assertTrue(divisions.contains("রাজশাহী"))
        assertTrue(divisions.contains("খুলনা"))
        assertTrue(divisions.contains("বরিশাল"))
        assertTrue(divisions.contains("রংপুর"))
        assertTrue(divisions.contains("ময়মনসিংহ"))
    }

    @Test
    fun userProfile_dataIntegrity() {
        val profile = UserProfile(
            uid = "user_123",
            name = "রহিম স্টোর",
            phone = "01700000000",
            email = "rohim@example.com",
            shopName = "মেসার্স রহিম ট্রেডার্স",
            district = "ঢাকা"
        )
        assertEquals("user_123", profile.uid)
        assertEquals("রহিম স্টোর", profile.name)
        assertEquals("মেসার্স রহিম ট্রেডার্স", profile.shopName)
        assertEquals("ঢাকা", profile.district)
    }

    @Test
    fun localeStrings_notEmptyAndFormattingCorrect() {
        assertTrue(LocaleStrings.APP_NAME_BN.isNotEmpty())
        assertTrue(LocaleStrings.NAV_HOME.isNotEmpty())
        assertEquals("৳", LocaleStrings.CURRENCY_SYMBOL)

        // Test Bangla digit conversion
        assertEquals("১২৩৪৫৬৭৮৯০", LocaleStrings.toBanglaDigits("1234567890"))
        
        // Test Taka formatting
        val formatted = LocaleStrings.formatTaka(15000)
        assertTrue(formatted.contains("৳"))
        assertTrue(formatted.contains("১৫,০০০"))
    }
}

