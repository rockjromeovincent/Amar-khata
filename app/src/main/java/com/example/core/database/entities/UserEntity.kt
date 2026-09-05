package com.example.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.UserProfile

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    val phone: String,
    val email: String,
    val shopName: String,
    val district: String,
    val password: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    fun toUserProfile(): UserProfile = UserProfile(
        uid = uid,
        name = name,
        phone = phone,
        email = email,
        shopName = shopName,
        district = district,
        registeredAt = registeredAt
    )
}
