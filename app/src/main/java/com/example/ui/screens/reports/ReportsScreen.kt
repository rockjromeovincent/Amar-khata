package com.example.ui.screens.reports

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.models.TransactionType
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange
import com.example.viewmodels.AccountingViewModel
import kotlinx.coroutines.launch

@Composable
fun ReportsScreen(
    accountingViewModel: AccountingViewModel? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val transactions = accountingViewModel?.allTransactions?.collectAsState()?.value ?: emptyList()
    val customers = accountingViewModel?.allCustomers?.collectAsState()?.value ?: emptyList()
    val suppliers = accountingViewModel?.allSuppliers?.collectAsState()?.value ?: emptyList()

    var totalIncome = 0.0
    var totalExpense = 0.0
    for (tx in transactions) {
        when (tx.type) {
            TransactionType.CASH_IN, TransactionType.CUSTOMER_PAYMENT -> totalIncome += tx.paidAmount
            TransactionType.CREDIT_SALE -> totalIncome += tx.paidAmount
            TransactionType.CASH_OUT, TransactionType.SUPPLIER_PAYMENT -> totalExpense += tx.paidAmount
            TransactionType.CREDIT_PURCHASE -> totalExpense += tx.paidAmount
        }
    }
    val netProfit = totalIncome - totalExpense
    val totalReceivable = customers.sumOf { it.totalDue.coerceAtLeast(0.0) }
    val totalPayable = suppliers.sumOf { it.totalPayable.coerceAtLeast(0.0) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = LocaleStrings.REPORTS_HEADER,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ব্যবসায়িক লাভ, বাকি ও ক্যাশ প্রবাহের সারাংশ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Summary Overview
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "মোট ব্যবসায়িক হিসাব ও লাভ-ক্ষতি",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ReportMetricItem(
                                title = "মোট আয়",
                                amount = LocaleStrings.formatTaka(totalIncome.toInt()),
                                color = CashInGreen
                            )
                            ReportMetricItem(
                                title = "মোট ব্যয়",
                                amount = LocaleStrings.formatTaka(totalExpense.toInt()),
                                color = CashOutRed
                            )
                            ReportMetricItem(
                                title = "নিট লাভ/ব্যালেন্স",
                                amount = LocaleStrings.formatTaka(netProfit.toInt()),
                                color = if (netProfit >= 0) CashInGreen else CashOutRed
                            )
                        }
                    }
                }
            }

            // Dues Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "বকেয়া ও দেনার বর্তমান স্থিতি",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ReportMetricItem(
                                title = "মোট বকেয়া (আমি পাব)",
                                amount = LocaleStrings.formatTaka(totalReceivable.toInt()),
                                color = DueOrange
                            )
                            ReportMetricItem(
                                title = "মোট দেনা (আমি দেব)",
                                amount = LocaleStrings.formatTaka(totalPayable.toInt()),
                                color = CashOutRed
                            )
                        }
                    }
                }
            }

            // Available Report Cards
            item {
                Text(
                    text = "রিপোর্টের ধরণ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                ReportTypeCard(
                    title = LocaleStrings.REPORT_DAILY_LEDGER,
                    subtitle = "দৈনিক কেনাবেচা ও খরচের পুঙ্খানুপুঙ্খ বিবরণ",
                    icon = Icons.Filled.MenuBook,
                    onDownload = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("দৈনিক খতিয়ান রিপোর্ট তৈরি হচ্ছে...")
                        }
                    }
                )
            }

            item {
                ReportTypeCard(
                    title = LocaleStrings.REPORT_CUSTOMER_LEDGER,
                    subtitle = "সকল কাস্টমারের বর্তমান বকেয়া ও দেনা তালিকা",
                    icon = Icons.Filled.Assessment,
                    onDownload = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("কাস্টমার বাকি রিপোর্ট তৈরি হচ্ছে...")
                        }
                    }
                )
            }

            item {
                ReportTypeCard(
                    title = LocaleStrings.REPORT_CASH_BOOK,
                    subtitle = "দৈনিক নগদ ও ব্যাংক জমার খতিয়ান বিবরণ",
                    icon = Icons.Filled.PieChart,
                    onDownload = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("ক্যাশ বই রিপোর্ট তৈরি হচ্ছে...")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ReportMetricItem(
    title: String,
    amount: String,
    color: Color
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 15.sp
            )
        )
    }
}

@Composable
fun ReportTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onDownload: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDownload,
                modifier = Modifier.testTag("download_report_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "ডাউনলোড",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
