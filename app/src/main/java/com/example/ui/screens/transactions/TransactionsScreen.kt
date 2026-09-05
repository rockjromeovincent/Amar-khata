package com.example.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.models.TransactionModel
import com.example.models.TransactionType
import com.example.ui.screens.transactions.components.AddCustomerBottomSheet
import com.example.ui.screens.transactions.components.AddSupplierBottomSheet
import com.example.ui.screens.transactions.components.QuickTransactionBottomSheet
import com.example.ui.screens.transactions.components.TransactionDetailsBottomSheet
import com.example.ui.screens.transactions.components.TransactionFormBottomSheet
import com.example.ui.screens.transactions.components.TransactionItemCard
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.viewmodels.AccountingViewModel

val DATE_FILTERS = listOf("আজ", "চলতি সপ্তাহ", "চলতি মাস", "সকল")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    accountingViewModel: AccountingViewModel
) {
    val filteredTransactions by accountingViewModel.filteredTransactions.collectAsState()
    val allCustomers by accountingViewModel.allCustomers.collectAsState()
    val allSuppliers by accountingViewModel.allSuppliers.collectAsState()
    val selectedDateFilter by accountingViewModel.selectedDateFilter.collectAsState()
    val selectedTypeFilter by accountingViewModel.selectedTypeFilter.collectAsState()
    val searchQuery by accountingViewModel.searchQuery.collectAsState()

    var showQuickMenu by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedDetailTransaction by remember { mutableStateOf<TransactionModel?>(null) }
    var editingTransaction by remember { mutableStateOf<TransactionModel?>(null) }
    var showAddCustomerSheet by remember { mutableStateOf(false) }
    var showAddSupplierSheet by remember { mutableStateOf(false) }

    val quickSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val formSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addCustomerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSupplierSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Calculate Inflow & Outflow for current filtered set
    var filteredInflow = 0.0
    var filteredOutflow = 0.0
    for (tx in filteredTransactions) {
        when (tx.type) {
            TransactionType.CASH_IN, TransactionType.CUSTOMER_PAYMENT -> filteredInflow += tx.paidAmount
            TransactionType.CREDIT_SALE -> filteredInflow += tx.paidAmount
            TransactionType.CASH_OUT, TransactionType.SUPPLIER_PAYMENT -> filteredOutflow += tx.paidAmount
            TransactionType.CREDIT_PURCHASE -> filteredOutflow += tx.paidAmount
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickMenu = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_transaction")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "+ লেনদেন")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ লেনদেন",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocaleStrings.TRANSACTION_HEADER,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${filteredTransactions.size} টি এন্ট্রি",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { accountingViewModel.setSearchQuery(it) },
                placeholder = { Text("কাস্টমার, বিবরণ বা টাকা দিয়ে খুঁজুন...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { accountingViewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_transactions")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Date Range Filter Chips (Row)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DATE_FILTERS.forEachIndexed { index, label ->
                    val isSelected = selectedDateFilter == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { accountingViewModel.setDateFilter(index) },
                        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Transaction Type Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isAllSelected = selectedTypeFilter == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = { accountingViewModel.setTypeFilter(null) },
                    label = { Text("সকল ধরন") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                TransactionType.entries.forEach { type ->
                    val isSelected = selectedTypeFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { accountingViewModel.setTypeFilter(if (isSelected) null else type) },
                        label = { Text(type.titleBn) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Summary Banner (Inflow vs Outflow for current view)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CashInGreen.copy(alpha = 0.15f))
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowDownward, contentDescription = null, tint = CashInGreen, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "মোট জমা", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = LocaleStrings.formatTaka(filteredInflow.toInt()), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = CashInGreen))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CashOutRed.copy(alpha = 0.15f))
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = null, tint = CashOutRed, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "মোট খরচ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = LocaleStrings.formatTaka(filteredOutflow.toInt()), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = CashOutRed))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transactions LazyColumn
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো লেনদেন পাওয়া যায়নি",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTransactions, key = { it.id }) { tx ->
                        TransactionItemCard(
                            transaction = tx,
                            onClick = { selectedDetailTransaction = tx }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
                    }
                }
            }
        }
    }

    // Quick Bottom Sheet
    if (showQuickMenu) {
        QuickTransactionBottomSheet(
            sheetState = quickSheetState,
            onDismiss = { showQuickMenu = false },
            onSelectType = { type ->
                showQuickMenu = false
                selectedTransactionType = type
            }
        )
    }

    // Transaction Form Bottom Sheet
    if (selectedTransactionType != null || editingTransaction != null) {
        val type = selectedTransactionType ?: editingTransaction!!.type
        TransactionFormBottomSheet(
            sheetState = formSheetState,
            initialType = type,
            existingTransaction = editingTransaction,
            customers = allCustomers,
            suppliers = allSuppliers,
            onDismiss = {
                selectedTransactionType = null
                editingTransaction = null
            },
            onQuickAddCustomer = { showAddCustomerSheet = true },
            onQuickAddSupplier = { showAddSupplierSheet = true },
            onSaveCashIn = { amount, source, date, note, method ->
                accountingViewModel.recordCashIn(amount, source, date, note, method)
            },
            onSaveCashOut = { amount, category, date, note, method ->
                accountingViewModel.recordCashOut(amount, category, date, note, method)
            },
            onSaveCreditSale = { customerId, customerName, amount, paid, date, note, method ->
                accountingViewModel.recordCreditSale(customerId, customerName, amount, paid, date, note, method)
            },
            onSaveCreditPurchase = { supplierId, supplierName, amount, paid, date, note, method ->
                accountingViewModel.recordCreditPurchase(supplierId, supplierName, amount, paid, date, note, method)
            },
            onSaveCustomerPayment = { customerId, customerName, amount, date, note, method ->
                accountingViewModel.recordCustomerPayment(customerId, customerName, amount, date, note, method)
            },
            onSaveSupplierPayment = { supplierId, supplierName, amount, date, note, method ->
                accountingViewModel.recordSupplierPayment(supplierId, supplierName, amount, date, note, method)
            },
            onUpdateTransaction = { updated ->
                accountingViewModel.updateTransaction(updated)
            }
        )
    }

    // Transaction Details Bottom Sheet
    if (selectedDetailTransaction != null) {
        TransactionDetailsBottomSheet(
            sheetState = detailsSheetState,
            transaction = selectedDetailTransaction!!,
            onDismiss = { selectedDetailTransaction = null },
            onEdit = {
                editingTransaction = selectedDetailTransaction
                selectedDetailTransaction = null
            },
            onDelete = {
                val id = selectedDetailTransaction!!.id
                accountingViewModel.deleteTransaction(id)
                selectedDetailTransaction = null
            }
        )
    }

    // Inline Add Customer Sheet
    if (showAddCustomerSheet) {
        AddCustomerBottomSheet(
            sheetState = addCustomerSheetState,
            onDismiss = { showAddCustomerSheet = false },
            onSaveCustomer = { name, phone, address, opening ->
                accountingViewModel.addCustomer(name, phone, address, opening)
            }
        )
    }

    // Inline Add Supplier Sheet
    if (showAddSupplierSheet) {
        AddSupplierBottomSheet(
            sheetState = addSupplierSheetState,
            onDismiss = { showAddSupplierSheet = false },
            onSaveSupplier = { name, phone, address, opening ->
                accountingViewModel.addSupplier(name, phone, address, opening)
            }
        )
    }
}
