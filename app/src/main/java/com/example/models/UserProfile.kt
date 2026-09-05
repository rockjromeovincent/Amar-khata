package com.example.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val shopName: String = "",
    val district: String = "",
    val registeredAt: Long = System.currentTimeMillis()
)
