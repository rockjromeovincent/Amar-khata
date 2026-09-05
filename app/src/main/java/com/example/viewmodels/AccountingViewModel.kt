package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.models.CustomerModel
import com.example.models.DashboardSummary
import com.example.models.LedgerEntry
import com.example.models.SupplierModel
import com.example.models.TransactionModel
import com.example.models.TransactionType
import com.example.repositories.AccountingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AccountingViewModel(
    private val repository: AccountingRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val dashboardSummary: StateFlow<DashboardSummary> = repository.dashboardSummaryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    val allTransactions: StateFlow<List<TransactionModel>> = repository.allTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomers: StateFlow<List<CustomerModel>> = repository.allCustomersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueCustomers: StateFlow<List<CustomerModel>> = repository.dueCustomersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSuppliers: StateFlow<List<SupplierModel>> = repository.allSuppliersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueSuppliers: StateFlow<List<SupplierModel>> = repository.dueSuppliersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters for Transaction Screen
    private val _selectedDateFilter = MutableStateFlow(0) // 0: Today, 1: This Week, 2: This Month, 3: All
    val selectedDateFilter: StateFlow<Int> = _selectedDateFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    val selectedTypeFilter: StateFlow<TransactionType?> = _selectedTypeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered Transactions Flow
    val filteredTransactions: StateFlow<List<TransactionModel>> = combine(
        allTransactions,
        _selectedDateFilter,
        _selectedTypeFilter,
        _searchQuery
    ) { txs, dateFilter, typeFilter, query ->
        var list = txs

        // 1. Date Filter
        val now = Calendar.getInstance()
        val startOfToday = getStartOfDay(now).timeInMillis
        val startOfWeek = getStartOfWeek(now).timeInMillis
        val startOfMonth = getStartOfMonth(now).timeInMillis

        list = when (dateFilter) {
            0 -> list.filter { it.date >= startOfToday }
            1 -> list.filter { it.date >= startOfWeek }
            2 -> list.filter { it.date >= startOfMonth }
            else -> list
        }

        // 2. Type Filter
        if (typeFilter != null) {
            list = list.filter { it.type == typeFilter }
        }

        // 3. Search Query Filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { tx ->
                tx.description.lowercase().contains(q) ||
                        (tx.customerName?.lowercase()?.contains(q) == true) ||
                        (tx.supplierName?.lowercase()?.contains(q) == true) ||
                        tx.categoryOrSource.lowercase().contains(q) ||
                        tx.amount.toString().contains(q)
            }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDateFilter(filterIndex: Int) {
        _selectedDateFilter.value = filterIndex
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedTypeFilter.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Customer & Supplier Operations ---

    fun addCustomer(
        name: String,
        phone: String,
        address: String = "",
        openingBalance: Double = 0.0,
        onSuccess: (CustomerModel) -> Unit = {}
    ) {
        viewModelScope.launch {
            val customer = repository.addCustomer(name, phone, address, openingBalance)
            onSuccess(customer)
        }
    }

    fun updateCustomer(customer: CustomerModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            onSuccess()
        }
    }

    fun deleteCustomer(customerId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteCustomer(customerId)
            onSuccess()
        }
    }

    fun addSupplier(
        name: String,
        phone: String,
        address: String = "",
        openingBalance: Double = 0.0,
        onSuccess: (SupplierModel) -> Unit = {}
    ) {
        viewModelScope.launch {
            val supplier = repository.addSupplier(name, phone, address, openingBalance)
            onSuccess(supplier)
        }
    }

    fun updateSupplier(supplier: SupplierModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateSupplier(supplier)
            onSuccess()
        }
    }

    fun deleteSupplier(supplierId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteSupplier(supplierId)
            onSuccess()
        }
    }

    // --- Ledger Queries ---

    fun getCustomerLedger(customerId: String): Flow<List<LedgerEntry>> {
        return repository.getCustomerLedgerFlow(customerId)
    }

    fun getSupplierLedger(supplierId: String): Flow<List<LedgerEntry>> {
        return repository.getSupplierLedgerFlow(supplierId)
    }

    // --- Transaction Actions ---

    fun recordCashIn(
        amount: Double,
        source: String,
        date: Long,
        note: String,
        paymentMethod: String = "নগদ (Cash)",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.recordCashIn(amount, source, date, note, paymentMethod)
            onSuccess()
        }
    }

    fun recordCashOut(
        amount: Double,
        category: String,
        date: Long,
        note: String,
        paymentMethod: String = "নগদ (Cash)",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.recordCashOut(amount, category, date, note, paymentMethod)
            onSuccess()
        }
    }

    fun recordCreditSale(
        customerId: String,
        customerName: String,
        amount: Double,
        paid: Double,
        date: Long,
        note: String,
        paymentMethod: String = "নগদ (Cash)",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.recordCreditSale(customerId, customerName, amount, paid, date, note, paymentMethod)
            onSuccess()
        }
    }

    fun recordCreditPurchase(
        supplierId: String,
        supplierName: String,
        amount: Double,
        paid: Double,
        date: Long,
        note: String,
        paymentMethod: String = "নগদ (Cash)",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.recordCreditPurchase(supplierId, supplierName, amount, paid, date, note, paymentMethod)
            onSuccess()
        }
    }

    fun recordCustomerPayment(
        customerId: String,
        customerName: String,
        amount: Double,
        date: Long,
        note: String,
        paymentMethod: String = "নগদ (Cash)",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.recordCustomerPayment(customerId, customerName, amount, date, note, paymentMethod)
            onSuccess()
        }
    }

    fun recordSupplierPayment(
        supplierId: String,
        supplierName: String,
        amount: Double,
        date: Long,
        note: String,
        paymentMethod: String = "নগদ (Cash)",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.recordSupplierPayment(supplierId, supplierName, amount, date, note, paymentMethod)
            onSuccess()
        }
    }

    fun updateTransaction(transaction: TransactionModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            onSuccess()
        }
    }

    fun deleteTransaction(transactionId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
            onSuccess()
        }
    }

    // Helper Date Calculations
    private fun getStartOfDay(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun getStartOfWeek(calendar: Calendar): Calendar {
        val cal = getStartOfDay(calendar)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        return cal
    }

    private fun getStartOfMonth(calendar: Calendar): Calendar {
        val cal = getStartOfDay(calendar)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal
    }

    class Factory(private val repository: AccountingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountingViewModel(repository) as T
        }
    }
}
