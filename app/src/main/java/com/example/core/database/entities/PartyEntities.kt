package com.example.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.CustomerModel
import com.example.models.SupplierModel

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val address: String = "",
    val openingBalance: Double = 0.0,
    val totalDue: Double = 0.0,
    val lastTransactionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toModel(): CustomerModel {
        return CustomerModel(
            id = id,
            name = name,
            phone = phone,
            address = address,
            openingBalance = openingBalance,
            totalDue = totalDue,
            lastTransactionDate = lastTransactionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromModel(model: CustomerModel): CustomerEntity {
            return CustomerEntity(
                id = model.id,
                name = model.name,
                phone = model.phone,
                address = model.address,
                openingBalance = model.openingBalance,
                totalDue = model.totalDue,
                lastTransactionDate = model.lastTransactionDate,
                createdAt = model.createdAt,
                updatedAt = model.updatedAt
            )
        }
    }
}

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val address: String = "",
    val openingBalance: Double = 0.0,
    val totalPayable: Double = 0.0,
    val lastTransactionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toModel(): SupplierModel {
        return SupplierModel(
            id = id,
            name = name,
            phone = phone,
            address = address,
            openingBalance = openingBalance,
            totalPayable = totalPayable,
            lastTransactionDate = lastTransactionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromModel(model: SupplierModel): SupplierEntity {
            return SupplierEntity(
                id = model.id,
                name = model.name,
                phone = model.phone,
                address = model.address,
                openingBalance = model.openingBalance,
                totalPayable = model.totalPayable,
                lastTransactionDate = model.lastTransactionDate,
                createdAt = model.createdAt,
                updatedAt = model.updatedAt
            )
        }
    }
}
