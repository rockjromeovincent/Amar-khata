package com.example.models

data class CustomerModel(
    val id: String,
    val name: String,
    val phone: String,
    val address: String = "",
    val openingBalance: Double = 0.0,
    val totalDue: Double = 0.0, // Amount customer owes the business (আমি পাব)
    val lastTransactionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class SupplierModel(
    val id: String,
    val name: String,
    val phone: String,
    val address: String = "",
    val openingBalance: Double = 0.0,
    val totalPayable: Double = 0.0, // Amount business owes the supplier (আমি দেব)
    val lastTransactionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
