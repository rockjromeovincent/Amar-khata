package com.example.ui.screens.customers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.models.CustomerModel
import com.example.models.SupplierModel
import com.example.models.TransactionType
import com.example.ui.screens.transactions.components.AddCustomerBottomSheet
import com.example.ui.screens.transactions.components.AddSupplierBottomSheet
import com.example.ui.screens.transactions.components.CustomerLedgerBottomSheet
import com.example.ui.screens.transactions.components.SupplierLedgerBottomSheet
import com.example.ui.screens.transactions.components.TransactionFormBottomSheet
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange
import com.example.viewmodels.AccountingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    accountingViewModel: AccountingViewModel
) {
    val context = LocalContext.current
    val allCustomers by accountingViewModel.allCustomers.collectAsState()
    val dueCustomers by accountingViewModel.dueCustomers.collectAsState()
    val allSuppliers by accountingViewModel.allSuppliers.collectAsState()
    val dueSuppliers by accountingViewModel.dueSuppliers.collectAsState()
    val dashboardSummary by accountingViewModel.dashboardSummary.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0: আমি পাব (Due Customers), 1: সকল কাস্টমার, 2: আমি দেব (Suppliers)
    var searchQuery by remember { mutableStateOf("") }

    var showAddCustomerSheet by remember { mutableStateOf(false) }
    var showAddSupplierSheet by remember { mutableStateOf(false) }

    var selectedCustomerForLedger by remember { mutableStateOf<CustomerModel?>(null) }
    var selectedSupplierForLedger by remember { mutableStateOf<SupplierModel?>(null) }

    var directTransactionType by remember { mutableStateOf<TransactionType?>(null) }
    var targetCustomerForTx by remember { mutableStateOf<CustomerModel?>(null) }
    var targetSupplierForTx by remember { mutableStateOf<SupplierModel?>(null) }

    val customerLedgerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val supplierLedgerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addCustomerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSupplierSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val directFormSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val dateFormat = SimpleDateFormat("dd MMM, yy", Locale.getDefault())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 2) {
                        showAddSupplierSheet = true
                    } else {
                        showAddCustomerSheet = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_party")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedTabIndex == 2) "+ নতুন সাপ্লায়ার" else "+ নতুন কাস্টমার",
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

            // Screen Header Title & Top Totals
            Text(
                text = "খাতা ও বাকি ব্যবস্থাপনা",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dues Summary Bar (আমি পাব vs আমি দেব)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "মোট পাবেন (আমি পাব)",
                            style = MaterialTheme.typography.labelSmall,
                            color = DueOrange
                        )
                        Text(
                            text = LocaleStrings.formatTaka(dashboardSummary.totalReceivable.toInt()),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DueOrange
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "মোট দেবেন (আমি দেব)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CashOutRed
                        )
                        Text(
                            text = LocaleStrings.formatTaka(dashboardSummary.totalPayable.toInt()),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CashOutRed
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(if (selectedTabIndex == 2) "সাপ্লায়ারের নাম বা ফোন নম্বর দিয়ে খুঁজুন..." else "কাস্টমারের নাম বা ফোন নম্বর দিয়ে খুঁজুন...")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
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
                    .testTag("input_search_customers")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Segmented Tabs
            val tabs = listOf(
                "আমি পাব (${dueCustomers.size})",
                "সকল কাস্টমার (${allCustomers.size})",
                "আমি দেব / সাপ্লায়ার (${allSuppliers.size})"
            )
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                ),
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List Content
            when (selectedTabIndex) {
                0, 1 -> {
                    // Customers List (Due or All)
                    val baseList = if (selectedTabIndex == 0) dueCustomers else allCustomers
                    val filteredCustomers = if (searchQuery.isBlank()) baseList else {
                        val q = searchQuery.trim().lowercase()
                        baseList.filter { it.name.lowercase().contains(q) || it.phone.contains(q) || it.address.lowercase().contains(q) }
                    }

                    if (filteredCustomers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedTabIndex == 0) "কোনো বকেয়া বাকি কাস্টমার নেই" else "কোনো কাস্টমার পাওয়া যায়নি",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCustomers, key = { it.id }) { cust ->
                                CustomerCard(
                                    customer = cust,
                                    dateFormat = dateFormat,
                                    onViewLedger = { selectedCustomerForLedger = cust },
                                    onCall = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${cust.phone}"))
                                        context.startActivity(intent)
                                    },
                                    onSendTagada = {
                                        val message = "জনাব ${cust.name}, আপনার কাছে আমাদের দোকানের মোট বকেয়া ${LocaleStrings.formatTaka(cust.totalDue.toInt())} টাকা। অনুগ্রহ করে বকেয়া পরিশোধ করুন। ধন্যবাদ - আমার খাতা"
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("sms:${cust.phone}")
                                            putExtra("sms_body", message)
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
                2 -> {
                    // Suppliers List
                    val filteredSuppliers = if (searchQuery.isBlank()) allSuppliers else {
                        val q = searchQuery.trim().lowercase()
                        allSuppliers.filter { it.name.lowercase().contains(q) || it.phone.contains(q) || it.address.lowercase().contains(q) }
                    }

                    if (filteredSuppliers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো সাপ্লায়ার পাওয়া যায়নি",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredSuppliers, key = { it.id }) { supp ->
                                SupplierCard(
                                    supplier = supp,
                                    dateFormat = dateFormat,
                                    onViewLedger = { selectedSupplierForLedger = supp },
                                    onCall = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${supp.phone}"))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Customer Ledger Bottom Sheet
    if (selectedCustomerForLedger != null) {
        val cust = selectedCustomerForLedger!!
        CustomerLedgerBottomSheet(
            sheetState = customerLedgerSheetState,
            customer = cust,
            ledgerFlow = accountingViewModel.getCustomerLedger(cust.id),
            onDismiss = { selectedCustomerForLedger = null },
            onCollectPayment = {
                targetCustomerForTx = cust
                directTransactionType = TransactionType.CUSTOMER_PAYMENT
            },
            onAddCreditSale = {
                targetCustomerForTx = cust
                directTransactionType = TransactionType.CREDIT_SALE
            }
        )
    }

    // Supplier Ledger Bottom Sheet
    if (selectedSupplierForLedger != null) {
        val supp = selectedSupplierForLedger!!
        SupplierLedgerBottomSheet(
            sheetState = supplierLedgerSheetState,
            supplier = supp,
            ledgerFlow = accountingViewModel.getSupplierLedger(supp.id),
            onDismiss = { selectedSupplierForLedger = null },
            onPaySupplier = {
                targetSupplierForTx = supp
                directTransactionType = TransactionType.SUPPLIER_PAYMENT
            },
            onAddCreditPurchase = {
                targetSupplierForTx = supp
                directTransactionType = TransactionType.CREDIT_PURCHASE
            }
        )
    }

    // Direct Transaction Form from Ledger
    if (directTransactionType != null) {
        TransactionFormBottomSheet(
            sheetState = directFormSheetState,
            initialType = directTransactionType!!,
            customers = allCustomers,
            suppliers = allSuppliers,
            onDismiss = {
                directTransactionType = null
                targetCustomerForTx = null
                targetSupplierForTx = null
            },
            onQuickAddCustomer = { showAddCustomerSheet = true },
            onQuickAddSupplier = { showAddSupplierSheet = true },
            onSaveCashIn = { a, s, d, n, m -> accountingViewModel.recordCashIn(a, s, d, n, m) },
            onSaveCashOut = { a, c, d, n, m -> accountingViewModel.recordCashOut(a, c, d, n, m) },
            onSaveCreditSale = { cId, cName, a, p, d, n, m -> accountingViewModel.recordCreditSale(cId, cName, a, p, d, n, m) },
            onSaveCreditPurchase = { sId, sName, a, p, d, n, m -> accountingViewModel.recordCreditPurchase(sId, sName, a, p, d, n, m) },
            onSaveCustomerPayment = { cId, cName, a, d, n, m -> accountingViewModel.recordCustomerPayment(cId, cName, a, d, n, m) },
            onSaveSupplierPayment = { sId, sName, a, d, n, m -> accountingViewModel.recordSupplierPayment(sId, sName, a, d, n, m) }
        )
    }

    // Add Customer Sheet
    if (showAddCustomerSheet) {
        AddCustomerBottomSheet(
            sheetState = addCustomerSheetState,
            onDismiss = { showAddCustomerSheet = false },
            onSaveCustomer = { name, phone, address, opening ->
                accountingViewModel.addCustomer(name, phone, address, opening)
            }
        )
    }

    // Add Supplier Sheet
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

@Composable
fun CustomerCard(
    customer: CustomerModel,
    dateFormat: SimpleDateFormat,
    onViewLedger: () -> Unit,
    onCall: () -> Unit,
    onSendTagada: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewLedger)
            .testTag("customer_card_${customer.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "${customer.phone}${if (customer.address.isNotBlank()) " • ${customer.address}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = LocaleStrings.formatTaka(customer.totalDue.toInt()),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (customer.totalDue > 0) DueOrange else CashInGreen
                        )
                    )
                    Text(
                        text = if (customer.totalDue > 0) "বকেয়া পাবে" else "পরিশোধিত",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (customer.totalDue > 0) DueOrange else CashInGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Actions: Call, Tagada SMS, Ledger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "কল", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = onSendTagada,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DueOrange),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(imageVector = Icons.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "তাগাদা পাঠান", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = onViewLedger,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Icon(imageVector = Icons.Filled.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "খতিয়ান", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun SupplierCard(
    supplier: SupplierModel,
    dateFormat: SimpleDateFormat,
    onViewLedger: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewLedger)
            .testTag("supplier_card_${supplier.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = supplier.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "${supplier.phone}${if (supplier.address.isNotBlank()) " • ${supplier.address}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = LocaleStrings.formatTaka(supplier.totalPayable.toInt()),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CashOutRed
                        )
                    )
                    Text(
                        text = "দেনা পাবেন",
                        style = MaterialTheme.typography.labelSmall.copy(color = CashOutRed)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Actions: Call & Ledger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "কল করুন", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = onViewLedger,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "মহাজন খতিয়ান", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
