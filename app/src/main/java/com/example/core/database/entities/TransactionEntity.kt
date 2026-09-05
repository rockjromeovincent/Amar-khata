package com.example.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.TransactionModel
import com.example.models.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val customerId: String? = null,
    val supplierId: String? = null,
    val customerName: String? = null,
    val supplierName: String? = null,
    val type: String, // from TransactionType.id
    val amount: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val description: String = "",
    val date: Long,
    val paymentMethod: String = "নগদ (Cash)",
    val categoryOrSource: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toModel(): TransactionModel {
        return TransactionModel(
            id = id,
            customerId = customerId,
            supplierId = supplierId,
            customerName = customerName,
            supplierName = supplierName,
            type = TransactionType.fromId(type),
            amount = amount,
            paidAmount = paidAmount,
            dueAmount = dueAmount,
            description = description,
            date = date,
            paymentMethod = paymentMethod,
            categoryOrSource = categoryOrSource,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromModel(model: TransactionModel): TransactionEntity {
            return TransactionEntity(
                id = model.id,
                customerId = model.customerId,
                supplierId = model.supplierId,
                customerName = model.customerName,
                supplierName = model.supplierName,
                type = model.type.id,
                amount = model.amount,
                paidAmount = model.paidAmount,
                dueAmount = model.dueAmount,
                description = model.description,
                date = model.date,
                paymentMethod = model.paymentMethod,
                categoryOrSource = model.categoryOrSource,
                createdAt = model.createdAt,
                updatedAt = model.updatedAt
            )
        }
    }
}
