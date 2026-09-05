package com.example.repositories

import com.example.core.database.AppDatabase
import com.example.core.database.entities.CustomerEntity
import com.example.core.database.entities.SupplierEntity
import com.example.core.database.entities.TransactionEntity
import com.example.models.CustomerModel
import com.example.models.DashboardSummary
import com.example.models.LedgerEntry
import com.example.models.SupplierModel
import com.example.models.TransactionModel
import com.example.models.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

class AccountingRepository(private val database: AppDatabase) {

    private val transactionDao = database.transactionDao()
    private val customerDao = database.customerDao()
    private val supplierDao = database.supplierDao()

    // --- Flows ---

    val allTransactionsFlow: Flow<List<TransactionModel>> =
        transactionDao.getAllTransactionsFlow()
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    val allCustomersFlow: Flow<List<CustomerModel>> =
        customerDao.getAllCustomersFlow()
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    val dueCustomersFlow: Flow<List<CustomerModel>> =
        customerDao.getDueCustomersFlow()
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    val allSuppliersFlow: Flow<List<SupplierModel>> =
        supplierDao.getAllSuppliersFlow()
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    val dueSuppliersFlow: Flow<List<SupplierModel>> =
        supplierDao.getDueSuppliersFlow()
            .map { list -> list.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    // Reactive Dashboard Summary
    val dashboardSummaryFlow: Flow<DashboardSummary> =
        combine(
            allTransactionsFlow,
            allCustomersFlow,
            allSuppliersFlow
        ) { transactions, customers, suppliers ->
            calculateDashboardSummary(transactions, customers, suppliers)
        }.flowOn(Dispatchers.Default)

    private fun calculateDashboardSummary(
        transactions: List<TransactionModel>,
        customers: List<CustomerModel>,
        suppliers: List<SupplierModel>
    ): DashboardSummary {
        var totalCashInflow = 0.0
        var totalCashOutflow = 0.0
        var todayIncome = 0.0
        var todayExpense = 0.0
        var todaySales = 0.0
        var todayTxCount = 0

        val startOfToday = getStartOfDayTimestamp()
        val endOfToday = getEndOfDayTimestamp()

        for (tx in transactions) {
            val isToday = tx.date in startOfToday..endOfToday
            if (isToday) {
                todayTxCount++
            }

            when (tx.type) {
                TransactionType.CASH_IN -> {
                    totalCashInflow += tx.paidAmount
                    if (isToday) todayIncome += tx.paidAmount
                }
                TransactionType.CASH_OUT -> {
                    totalCashOutflow += tx.paidAmount
                    if (isToday) todayExpense += tx.paidAmount
                }
                TransactionType.CREDIT_SALE -> {
                    totalCashInflow += tx.paidAmount
                    if (isToday) {
                        todayIncome += tx.paidAmount
                        todaySales += tx.amount
                    }
                }
                TransactionType.CREDIT_PURCHASE -> {
                    totalCashOutflow += tx.paidAmount
                    if (isToday) todayExpense += tx.paidAmount
                }
                TransactionType.CUSTOMER_PAYMENT -> {
                    totalCashInflow += tx.paidAmount
                    if (isToday) todayIncome += tx.paidAmount
                }
                TransactionType.SUPPLIER_PAYMENT -> {
                    totalCashOutflow += tx.paidAmount
                    if (isToday) todayExpense += tx.paidAmount
                }
            }
        }

        val totalReceivable = customers.sumOf { it.totalDue.coerceAtLeast(0.0) }
        val totalPayable = suppliers.sumOf { it.totalPayable.coerceAtLeast(0.0) }
        val cashBalance = totalCashInflow - totalCashOutflow

        return DashboardSummary(
            cashBalance = cashBalance,
            totalReceivable = totalReceivable,
            totalPayable = totalPayable,
            todayIncome = todayIncome,
            todayExpense = todayExpense,
            todaySales = todaySales,
            todayTransactionCount = todayTxCount
        )
    }

    // --- Customer Operations ---

    suspend fun addCustomer(
        name: String,
        phone: String,
        address: String = "",
        openingBalance: Double = 0.0
    ): CustomerModel = withContext(Dispatchers.IO) {
        val customer = CustomerEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            phone = phone.trim(),
            address = address.trim(),
            openingBalance = openingBalance,
            totalDue = openingBalance,
            lastTransactionDate = if (openingBalance != 0.0) System.currentTimeMillis() else null
        )
        customerDao.insertCustomer(customer)
        customer.toModel()
    }

    suspend fun updateCustomer(customer: CustomerModel) = withContext(Dispatchers.IO) {
        customerDao.updateCustomer(CustomerEntity.fromModel(customer))
        recalculateCustomerBalance(customer.id)
    }

    suspend fun deleteCustomer(customerId: String) = withContext(Dispatchers.IO) {
        customerDao.deleteCustomerById(customerId)
    }

    suspend fun getCustomerById(customerId: String): CustomerModel? = withContext(Dispatchers.IO) {
        customerDao.getCustomerById(customerId)?.toModel()
    }

    // --- Supplier Operations ---

    suspend fun addSupplier(
        name: String,
        phone: String,
        address: String = "",
        openingBalance: Double = 0.0
    ): SupplierModel = withContext(Dispatchers.IO) {
        val supplier = SupplierEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            phone = phone.trim(),
            address = address.trim(),
            openingBalance = openingBalance,
            totalPayable = openingBalance,
            lastTransactionDate = if (openingBalance != 0.0) System.currentTimeMillis() else null
        )
        supplierDao.insertSupplier(supplier)
        supplier.toModel()
    }

    suspend fun updateSupplier(supplier: SupplierModel) = withContext(Dispatchers.IO) {
        supplierDao.updateSupplier(SupplierEntity.fromModel(supplier))
        recalculateSupplierBalance(supplier.id)
    }

    suspend fun deleteSupplier(supplierId: String) = withContext(Dispatchers.IO) {
        supplierDao.deleteSupplierById(supplierId)
    }

    suspend fun getSupplierById(supplierId: String): SupplierModel? = withContext(Dispatchers.IO) {
        supplierDao.getSupplierById(supplierId)?.toModel()
    }

    // --- Transaction Operations ---

    suspend fun recordCashIn(
        amount: Double,
        source: String = "",
        date: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "নগদ (Cash)"
    ): TransactionModel = withContext(Dispatchers.IO) {
        val tx = TransactionModel(
            id = UUID.randomUUID().toString(),
            type = TransactionType.CASH_IN,
            amount = amount,
            paidAmount = amount,
            dueAmount = 0.0,
            description = note.ifBlank { source.ifBlank { "টাকা গ্রহণ" } },
            categoryOrSource = source,
            date = date,
            paymentMethod = paymentMethod
        )
        transactionDao.insertTransaction(TransactionEntity.fromModel(tx))
        tx
    }

    suspend fun recordCashOut(
        amount: Double,
        category: String = "",
        date: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "নগদ (Cash)"
    ): TransactionModel = withContext(Dispatchers.IO) {
        val tx = TransactionModel(
            id = UUID.randomUUID().toString(),
            type = TransactionType.CASH_OUT,
            amount = amount,
            paidAmount = amount,
            dueAmount = 0.0,
            description = note.ifBlank { category.ifBlank { "খরচ বাবদ পরিশোধ" } },
            categoryOrSource = category,
            date = date,
            paymentMethod = paymentMethod
        )
        transactionDao.insertTransaction(TransactionEntity.fromModel(tx))
        tx
    }

    suspend fun recordCreditSale(
        customerId: String,
        customerName: String,
        amount: Double,
        paid: Double,
        date: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "নগদ (Cash)"
    ): TransactionModel = withContext(Dispatchers.IO) {
        val due = (amount - paid).coerceAtLeast(0.0)
        val tx = TransactionModel(
            id = UUID.randomUUID().toString(),
            customerId = customerId,
            customerName = customerName,
            type = TransactionType.CREDIT_SALE,
            amount = amount,
            paidAmount = paid,
            dueAmount = due,
            description = note.ifBlank { "বাকিতে পণ্য বিক্রয়" },
            date = date,
            paymentMethod = paymentMethod
        )
        transactionDao.insertTransaction(TransactionEntity.fromModel(tx))
        recalculateCustomerBalance(customerId)
        tx
    }

    suspend fun recordCreditPurchase(
        supplierId: String,
        supplierName: String,
        amount: Double,
        paid: Double,
        date: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "নগদ (Cash)"
    ): TransactionModel = withContext(Dispatchers.IO) {
        val due = (amount - paid).coerceAtLeast(0.0)
        val tx = TransactionModel(
            id = UUID.randomUUID().toString(),
            supplierId = supplierId,
            supplierName = supplierName,
            type = TransactionType.CREDIT_PURCHASE,
            amount = amount,
            paidAmount = paid,
            dueAmount = due,
            description = note.ifBlank { "বাকিতে পণ্য ক্রয়" },
            date = date,
            paymentMethod = paymentMethod
        )
        transactionDao.insertTransaction(TransactionEntity.fromModel(tx))
        recalculateSupplierBalance(supplierId)
        tx
    }

    suspend fun recordCustomerPayment(
        customerId: String,
        customerName: String,
        amount: Double,
        date: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "নগদ (Cash)"
    ): TransactionModel = withContext(Dispatchers.IO) {
        val tx = TransactionModel(
            id = UUID.randomUUID().toString(),
            customerId = customerId,
            customerName = customerName,
            type = TransactionType.CUSTOMER_PAYMENT,
            amount = amount,
            paidAmount = amount,
            dueAmount = 0.0,
            description = note.ifBlank { "কাস্টমার থেকে বকেয়া আদায়" },
            date = date,
            paymentMethod = paymentMethod
        )
        transactionDao.insertTransaction(TransactionEntity.fromModel(tx))
        recalculateCustomerBalance(customerId)
        tx
    }

    suspend fun recordSupplierPayment(
        supplierId: String,
        supplierName: String,
        amount: Double,
        date: Long = System.currentTimeMillis(),
        note: String = "",
        paymentMethod: String = "নগদ (Cash)"
    ): TransactionModel = withContext(Dispatchers.IO) {
        val tx = TransactionModel(
            id = UUID.randomUUID().toString(),
            supplierId = supplierId,
            supplierName = supplierName,
            type = TransactionType.SUPPLIER_PAYMENT,
            amount = amount,
            paidAmount = amount,
            dueAmount = 0.0,
            description = note.ifBlank { "সাপ্লায়ারকে বকেয়া পরিশোধ" },
            date = date,
            paymentMethod = paymentMethod
        )
        transactionDao.insertTransaction(TransactionEntity.fromModel(tx))
        recalculateSupplierBalance(supplierId)
        tx
    }

    suspend fun updateTransaction(transaction: TransactionModel) = withContext(Dispatchers.IO) {
        val oldTx = transactionDao.getTransactionById(transaction.id)?.toModel()
        transactionDao.updateTransaction(TransactionEntity.fromModel(transaction))

        // Recalculate customer / supplier balances
        oldTx?.customerId?.let { recalculateCustomerBalance(it) }
        transaction.customerId?.let { if (it != oldTx?.customerId) recalculateCustomerBalance(it) }

        oldTx?.supplierId?.let { recalculateSupplierBalance(it) }
        transaction.supplierId?.let { if (it != oldTx?.supplierId) recalculateSupplierBalance(it) }
    }

    suspend fun deleteTransaction(transactionId: String) = withContext(Dispatchers.IO) {
        val tx = transactionDao.getTransactionById(transactionId)?.toModel()
        transactionDao.deleteTransactionById(transactionId)

        tx?.customerId?.let { recalculateCustomerBalance(it) }
        tx?.supplierId?.let { recalculateSupplierBalance(it) }
    }

    // --- Ledger Calculations ---

    fun getCustomerLedgerFlow(customerId: String): Flow<List<LedgerEntry>> {
        return combine(
            customerDao.getAllCustomersFlow(),
            transactionDao.getTransactionsForCustomerFlow(customerId)
        ) { customers, txEntities ->
            val customer = customers.find { it.id == customerId }
            val opening = customer?.openingBalance ?: 0.0
            val entries = mutableListOf<LedgerEntry>()

            var runningBalance = opening
            val sortedTxs = txEntities.sortedWith(compareBy({ it.date }, { it.createdAt }))

            for (entity in sortedTxs) {
                val tx = entity.toModel()
                when (tx.type) {
                    TransactionType.CREDIT_SALE -> {
                        val debit = tx.amount // মোট বাকিতে বিক্রির পরিমাণ
                        val credit = tx.paidAmount // তাৎক্ষণিক নগদ জমা
                        runningBalance += tx.dueAmount
                        entries.add(
                            LedgerEntry(
                                transactionId = tx.id,
                                date = tx.date,
                                description = tx.description.ifBlank { "বাকিতে পণ্য বিক্রয়" },
                                type = tx.type,
                                debitAmount = debit,
                                creditAmount = credit,
                                runningBalance = runningBalance
                            )
                        )
                    }
                    TransactionType.CUSTOMER_PAYMENT -> {
                        runningBalance -= tx.amount
                        entries.add(
                            LedgerEntry(
                                transactionId = tx.id,
                                date = tx.date,
                                description = tx.description.ifBlank { "বকেয়া আদায়" },
                                type = tx.type,
                                debitAmount = 0.0,
                                creditAmount = tx.amount,
                                runningBalance = runningBalance
                            )
                        )
                    }
                    else -> {}
                }
            }
            entries.reversed() // Show newest first in UI
        }.flowOn(Dispatchers.Default)
    }

    fun getSupplierLedgerFlow(supplierId: String): Flow<List<LedgerEntry>> {
        return combine(
            supplierDao.getAllSuppliersFlow(),
            transactionDao.getTransactionsForSupplierFlow(supplierId)
        ) { suppliers, txEntities ->
            val supplier = suppliers.find { it.id == supplierId }
            val opening = supplier?.openingBalance ?: 0.0
            val entries = mutableListOf<LedgerEntry>()

            var runningBalance = opening
            val sortedTxs = txEntities.sortedWith(compareBy({ it.date }, { it.createdAt }))

            for (entity in sortedTxs) {
                val tx = entity.toModel()
                when (tx.type) {
                    TransactionType.CREDIT_PURCHASE -> {
                        val debit = tx.amount
                        val credit = tx.paidAmount
                        runningBalance += tx.dueAmount
                        entries.add(
                            LedgerEntry(
                                transactionId = tx.id,
                                date = tx.date,
                                description = tx.description.ifBlank { "বাকিতে ক্রয়" },
                                type = tx.type,
                                debitAmount = debit,
                                creditAmount = credit,
                                runningBalance = runningBalance
                            )
                        )
                    }
                    TransactionType.SUPPLIER_PAYMENT -> {
                        runningBalance -= tx.amount
                        entries.add(
                            LedgerEntry(
                                transactionId = tx.id,
                                date = tx.date,
                                description = tx.description.ifBlank { "বকেয়া পরিশোধ" },
                                type = tx.type,
                                debitAmount = 0.0,
                                creditAmount = tx.amount,
                                runningBalance = runningBalance
                            )
                        )
                    }
                    else -> {}
                }
            }
            entries.reversed()
        }.flowOn(Dispatchers.Default)
    }

    suspend fun recalculateCustomerBalance(customerId: String) = withContext(Dispatchers.IO) {
        val customer = customerDao.getCustomerById(customerId) ?: return@withContext
        val txs = transactionDao.getTransactionsForCustomer(customerId)

        var totalDue = customer.openingBalance
        var lastDate = if (customer.openingBalance != 0.0) customer.createdAt else null

        for (entity in txs) {
            val tx = entity.toModel()
            when (tx.type) {
                TransactionType.CREDIT_SALE -> {
                    totalDue += tx.dueAmount
                    lastDate = tx.date
                }
                TransactionType.CUSTOMER_PAYMENT -> {
                    totalDue -= tx.amount
                    lastDate = tx.date
                }
                else -> {}
            }
        }

        customerDao.updateCustomerDue(
            id = customerId,
            newDue = totalDue,
            lastDate = lastDate ?: System.currentTimeMillis()
        )
    }

    suspend fun recalculateSupplierBalance(supplierId: String) = withContext(Dispatchers.IO) {
        val supplier = supplierDao.getSupplierById(supplierId) ?: return@withContext
        val txs = transactionDao.getTransactionsForSupplier(supplierId)

        var totalPayable = supplier.openingBalance
        var lastDate = if (supplier.openingBalance != 0.0) supplier.createdAt else null

        for (entity in txs) {
            val tx = entity.toModel()
            when (tx.type) {
                TransactionType.CREDIT_PURCHASE -> {
                    totalPayable += tx.dueAmount
                    lastDate = tx.date
                }
                TransactionType.SUPPLIER_PAYMENT -> {
                    totalPayable -= tx.amount
                    lastDate = tx.date
                }
                else -> {}
            }
        }

        supplierDao.updateSupplierPayable(
            id = supplierId,
            newPayable = totalPayable,
            lastDate = lastDate ?: System.currentTimeMillis()
        )
    }

    // --- Sample Data Seeder for First Launch ---
    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val customers = customerDao.getAllCustomers()
        if (customers.isNotEmpty()) return@withContext

        // Create sample customers
        val c1 = CustomerEntity(
            id = "c_1",
            name = "আলমগীর হোসেন",
            phone = "01711223344",
            address = "মিরপুর-১০, ঢাকা",
            openingBalance = 5000.0,
            totalDue = 6000.0,
            lastTransactionDate = System.currentTimeMillis()
        )
        val c2 = CustomerEntity(
            id = "c_2",
            name = "করিম মিয়া",
            phone = "01819887766",
            address = "উত্তরা, ঢাকা",
            openingBalance = 0.0,
            totalDue = 3200.0,
            lastTransactionDate = System.currentTimeMillis() - 86400000L
        )
        val c3 = CustomerEntity(
            id = "c_3",
            name = "রফিকুল ইসলাম",
            phone = "01912345678",
            address = "ধানমন্ডি, ঢাকা",
            openingBalance = 0.0,
            totalDue = 1500.0,
            lastTransactionDate = System.currentTimeMillis() - 172800000L
        )
        customerDao.insertAll(listOf(c1, c2, c3))

        // Create sample suppliers
        val s1 = SupplierEntity(
            id = "s_1",
            name = "মেসার্স জামান ট্রেডার্স",
            phone = "01733445566",
            address = "চকবাজার, ঢাকা",
            openingBalance = 8000.0,
            totalPayable = 6000.0,
            lastTransactionDate = System.currentTimeMillis()
        )
        val s2 = SupplierEntity(
            id = "s_2",
            name = "মেঘনা ডিস্ট্রিবিউটরস",
            phone = "01855667788",
            address = "তেজগাঁও, ঢাকা",
            openingBalance = 0.0,
            totalPayable = 4500.0,
            lastTransactionDate = System.currentTimeMillis() - 86400000L
        )
        supplierDao.insertAll(listOf(s1, s2))

        // Create sample transactions
        val now = System.currentTimeMillis()
        val tx1 = TransactionEntity(
            id = "tx_1",
            customerId = "c_1",
            customerName = "আলমগীর হোসেন",
            type = TransactionType.CREDIT_SALE.id,
            amount = 2000.0,
            paidAmount = 1000.0,
            dueAmount = 1000.0,
            description = "চাল ও তেল বাকিতে বিক্রয়",
            date = now - 3600000L,
            paymentMethod = "নগদ (Cash)"
        )
        val tx2 = TransactionEntity(
            id = "tx_2",
            type = TransactionType.CASH_IN.id,
            amount = 4500.0,
            paidAmount = 4500.0,
            dueAmount = 0.0,
            description = "দোকানের নগদ দৈনিক বিক্রি",
            categoryOrSource = "দৈনিক বিক্রি",
            date = now - 7200000L,
            paymentMethod = "নগদ (Cash)"
        )
        val tx3 = TransactionEntity(
            id = "tx_3",
            type = TransactionType.CASH_OUT.id,
            amount = 800.0,
            paidAmount = 800.0,
            dueAmount = 0.0,
            description = "দোকানের বিদ্যুৎ বিল পরিশোধ",
            categoryOrSource = "বিদ্যুৎ বিল",
            date = now - 10800000L,
            paymentMethod = "bKash"
        )
        val tx4 = TransactionEntity(
            id = "tx_4",
            supplierId = "s_1",
            supplierName = "মেসার্স জামান ট্রেডার্স",
            type = TransactionType.SUPPLIER_PAYMENT.id,
            amount = 2000.0,
            paidAmount = 2000.0,
            dueAmount = 0.0,
            description = "সাপ্লায়ার দেনা বাবদ পরিশোধ",
            date = now - 14400000L,
            paymentMethod = "নগদ (Cash)"
        )
        val tx5 = TransactionEntity(
            id = "tx_5",
            customerId = "c_2",
            customerName = "করিম মিয়া",
            type = TransactionType.CREDIT_SALE.id,
            amount = 3200.0,
            paidAmount = 0.0,
            dueAmount = 3200.0,
            description = "নিত্যপ্রয়োজনীয় পণ্য বাকি",
            date = now - 86400000L,
            paymentMethod = "বাকি"
        )
        transactionDao.insertAll(listOf(tx1, tx2, tx3, tx4, tx5))
    }

    private fun getStartOfDayTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDayTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
