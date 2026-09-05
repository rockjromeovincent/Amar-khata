package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.core.database.entities.CustomerEntity
import com.example.core.database.entities.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY totalDue DESC, name ASC")
    fun getAllCustomersFlow(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers ORDER BY totalDue DESC, name ASC")
    suspend fun getAllCustomers(): List<CustomerEntity>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE totalDue > 0 ORDER BY totalDue DESC")
    fun getDueCustomersFlow(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE totalDue < 0 ORDER BY totalDue ASC")
    fun getPayableCustomersFlow(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Query("UPDATE customers SET totalDue = :newDue, lastTransactionDate = :lastDate, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCustomerDue(id: String, newDue: Double, lastDate: Long, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomerById(id: String)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY totalPayable DESC, name ASC")
    fun getAllSuppliersFlow(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers ORDER BY totalPayable DESC, name ASC")
    suspend fun getAllSuppliers(): List<SupplierEntity>

    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    suspend fun getSupplierById(id: String): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE totalPayable > 0 ORDER BY totalPayable DESC")
    fun getDueSuppliersFlow(): Flow<List<SupplierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suppliers: List<SupplierEntity>)

    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)

    @Query("UPDATE suppliers SET totalPayable = :newPayable, lastTransactionDate = :lastDate, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSupplierPayable(id: String, newPayable: Double, lastDate: Long, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)

    @Query("DELETE FROM suppliers WHERE id = :id")
    suspend fun deleteSupplierById(id: String)
}
