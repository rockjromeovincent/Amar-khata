package com.example.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.localization.LocaleStrings
import com.example.models.TransactionModel
import com.example.models.TransactionType
import com.example.ui.components.FintechMetricCard
import com.example.ui.screens.transactions.components.QuickTransactionBottomSheet
import com.example.ui.screens.transactions.components.TransactionDetailsBottomSheet
import com.example.ui.screens.transactions.components.TransactionFormBottomSheet
import com.example.ui.screens.transactions.components.TransactionItemCard
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange
import com.example.viewmodels.AccountingViewModel
import com.example.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    accountingViewModel: AccountingViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val userProfile by mainViewModel.userProfile.collectAsState()
    val dashboardSummary by accountingViewModel.dashboardSummary.collectAsState()
    val allTransactions by accountingViewModel.allTransactions.collectAsState()
    val customers by accountingViewModel.allCustomers.collectAsState()
    val suppliers by accountingViewModel.allSuppliers.collectAsState()

    var showQuickMenu by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedDetailTransaction by remember { mutableStateOf<TransactionModel?>(null) }
    var editingTransaction by remember { mutableStateOf<TransactionModel?>(null) }

    val quickSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val formSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val recentTransactions = allTransactions.take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Top App Bar / Business Header Banner
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.amar_khata_logo_1788239859198),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userProfile.shopName.ifBlank { "আমার ব্যবসা প্রতিষ্ঠান" },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = Color.White
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${userProfile.district.ifBlank { "ঢাকা" }} • প্রোপাইটার: ${userProfile.name.ifBlank { "ব্যবহারকারী" }}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "নোটিফিকেশন",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Hero Net Balance Card inside Banner
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "চলতি নিট ক্যাশ ব্যালেন্স",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = LocaleStrings.formatTaka(dashboardSummary.cashBalance.toInt()),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 26.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .clickable { showQuickMenu = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+ লেনদেন",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Financial Overview Grid (2x2) with live calculated numbers
        item {
            Text(
                text = LocaleStrings.TODAY_SUMMARY,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FintechMetricCard(
                    title = LocaleStrings.TODAY_SALE,
                    amount = LocaleStrings.formatTaka(dashboardSummary.todaySales.toInt()),
                    subtitle = "আজকের মোট বিক্রি",
                    accentColor = CashInGreen,
                    icon = Icons.Filled.ArrowDownward,
                    modifier = Modifier.weight(1f)
                )

                FintechMetricCard(
                    title = "মোট পাবেন",
                    amount = LocaleStrings.formatTaka(dashboardSummary.totalReceivable.toInt()),
                    subtitle = "কাস্টমার বকেয়া",
                    accentColor = DueOrange,
                    icon = Icons.Filled.Notifications,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FintechMetricCard(
                    title = "মোট দেবেন",
                    amount = LocaleStrings.formatTaka(dashboardSummary.totalPayable.toInt()),
                    subtitle = "সাপ্লায়ার দেনা",
                    accentColor = CashOutRed,
                    icon = Icons.Filled.ArrowUpward,
                    modifier = Modifier.weight(1f)
                )

                FintechMetricCard(
                    title = LocaleStrings.NET_CASH,
                    amount = LocaleStrings.formatTaka(dashboardSummary.cashBalance.toInt()),
                    subtitle = "হাতে নগদ",
                    accentColor = MaterialTheme.colorScheme.primary,
                    icon = Icons.Filled.AccountBalanceWallet,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = LocaleStrings.QUICK_ACTIONS,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    title = LocaleStrings.ADD_CUSTOMER,
                    icon = Icons.Filled.PersonAdd,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateToTab(2) } // Navigate to Customers tab
                )

                QuickActionItem(
                    title = "টাকা পেলাম",
                    icon = Icons.Filled.ArrowDownward,
                    color = CashInGreen,
                    onClick = { selectedTransactionType = TransactionType.CASH_IN }
                )

                QuickActionItem(
                    title = "টাকা দিলাম",
                    icon = Icons.Filled.ArrowUpward,
                    color = CashOutRed,
                    onClick = { selectedTransactionType = TransactionType.CASH_OUT }
                )

                QuickActionItem(
                    title = "বাকিতে বিক্রি",
                    icon = Icons.Filled.TrendingUp,
                    color = DueOrange,
                    onClick = { selectedTransactionType = TransactionType.CREDIT_SALE }
                )
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocaleStrings.RECENT_TRANSACTIONS,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = LocaleStrings.VIEW_ALL,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToTab(1) }
                        .padding(4.dp)
                )
            }
        }

        // Live Transactions List
        if (recentTransactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "এখনও কোনো লেনদেন এন্ট্রি করা হয়নি। + লেনদেন চাপুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    onClick = { selectedDetailTransaction = tx }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
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
            customers = customers,
            suppliers = suppliers,
            onDismiss = {
                selectedTransactionType = null
                editingTransaction = null
            },
            onQuickAddCustomer = { onNavigateToTab(2) },
            onQuickAddSupplier = { onNavigateToTab(2) },
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
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
