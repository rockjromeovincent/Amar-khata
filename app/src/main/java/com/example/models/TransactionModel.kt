package com.example.models

data class TransactionModel(
    val id: String,
    val customerId: String? = null,
    val supplierId: String? = null,
    val customerName: String? = null,
    val supplierName: String? = null,
    val type: TransactionType,
    val amount: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val description: String = "",
    val date: Long,
    val paymentMethod: String = "নগদ (Cash)",
    val categoryOrSource: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
