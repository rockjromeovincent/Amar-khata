package com.example.ui.screens.transactions.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.models.TransactionModel
import com.example.models.TransactionType
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TransactionFlowFilter {
    ALL,
    INCOME_ONLY,
    EXPENSE_ONLY
}

/**
 * List view of recent transactions sorted by date, featuring prominent
 * color-coded icons to distinctly differentiate income from expenses.
 */
@Composable
fun RecentTransactionsListView(
    transactions: List<TransactionModel>,
    onTransactionClick: (TransactionModel) -> Unit,
    modifier: Modifier = Modifier,
    maxItems: Int? = null,
    showControls: Boolean = true,
    showDateHeaders: Boolean = true,
    onViewAllClick: (() -> Unit)? = null
) {
    var sortDescending by remember { mutableStateOf(true) } // true: Newest first (recent)
    var selectedFlowFilter by remember { mutableStateOf(TransactionFlowFilter.ALL) }

    // 1. Filter by flow (All / Income / Expense)
    val filteredList = remember(transactions, selectedFlowFilter) {
        when (selectedFlowFilter) {
            TransactionFlowFilter.ALL -> transactions
            TransactionFlowFilter.INCOME_ONLY -> transactions.filter { it.isIncome }
            TransactionFlowFilter.EXPENSE_ONLY -> transactions.filter { !it.isIncome }
        }
    }

    // 2. Sort by date
    val sortedList = remember(filteredList, sortDescending) {
        if (sortDescending) {
            filteredList.sortedWith(
                compareByDescending<TransactionModel> { it.date }
                    .thenByDescending { it.createdAt }
            )
        } else {
            filteredList.sortedWith(
                compareBy<TransactionModel> { it.date }
                    .thenBy { it.createdAt }
            )
        }
    }

    // 3. Apply optional limit
    val displayList = remember(sortedList, maxItems) {
        if (maxItems != null && maxItems > 0) {
            sortedList.take(maxItems)
        } else {
            sortedList
        }
    }

    // 4. Group by day
    val dayFormatKey = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }
    val groupedByDate = remember(displayList) {
        displayList.groupBy { tx -> dayFormatKey.format(Date(tx.date)) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Optional Controls: Sort Toggle & Income/Expense Filters
        if (showControls) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Top Row: Section Title + Sort Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "সাম্প্রতিক লেনদেন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "${displayList.size} টি",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Sort Order Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .clickable { sortDescending = !sortDescending }
                            .testTag("btn_toggle_sort_date")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapVert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (sortDescending) "তারিখ: নতুন আগে" else "তারিখ: পুরাতন আগে",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Income / Expense Filter Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All Chip
                    FilterChip(
                        selected = selectedFlowFilter == TransactionFlowFilter.ALL,
                        onClick = { selectedFlowFilter = TransactionFlowFilter.ALL },
                        label = {
                            Text(
                                text = "সকল (${transactions.size})",
                                fontWeight = if (selectedFlowFilter == TransactionFlowFilter.ALL) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("filter_all_transactions")
                    )

                    // Income Only Chip (Color Coded Green)
                    val incomeCount = transactions.count { it.isIncome }
                    FilterChip(
                        selected = selectedFlowFilter == TransactionFlowFilter.INCOME_ONLY,
                        onClick = { selectedFlowFilter = TransactionFlowFilter.INCOME_ONLY },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDownward,
                                contentDescription = null,
                                tint = if (selectedFlowFilter == TransactionFlowFilter.INCOME_ONLY) Color.White else CashInGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "আয় / জমা ($incomeCount)",
                                fontWeight = if (selectedFlowFilter == TransactionFlowFilter.INCOME_ONLY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CashInGreen,
                            selectedLabelColor = Color.White,
                            containerColor = CashInGreen.copy(alpha = 0.08f),
                            labelColor = CashInGreen
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedFlowFilter == TransactionFlowFilter.INCOME_ONLY) CashInGreen else CashInGreen.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("filter_income_only")
                    )

                    // Expense Only Chip (Color Coded Red)
                    val expenseCount = transactions.count { !it.isIncome }
                    FilterChip(
                        selected = selectedFlowFilter == TransactionFlowFilter.EXPENSE_ONLY,
                        onClick = { selectedFlowFilter = TransactionFlowFilter.EXPENSE_ONLY },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowUpward,
                                contentDescription = null,
                                tint = if (selectedFlowFilter == TransactionFlowFilter.EXPENSE_ONLY) Color.White else CashOutRed,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "ব্যয় / খরচ ($expenseCount)",
                                fontWeight = if (selectedFlowFilter == TransactionFlowFilter.EXPENSE_ONLY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CashOutRed,
                            selectedLabelColor = Color.White,
                            containerColor = CashOutRed.copy(alpha = 0.08f),
                            labelColor = CashOutRed
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedFlowFilter == TransactionFlowFilter.EXPENSE_ONLY) CashOutRed else CashOutRed.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("filter_expense_only")
                    )
                }
            }
        }

        // Empty State
        if (displayList.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (selectedFlowFilter) {
                            TransactionFlowFilter.INCOME_ONLY -> "কোনো আয়ের লেনদেন পাওয়া যায়নি"
                            TransactionFlowFilter.EXPENSE_ONLY -> "কোনো ব্যয়ের লেনদেন পাওয়া যায়নি"
                            TransactionFlowFilter.ALL -> "এখনও কোনো লেনদেন রেকর্ড করা হয়নি"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "নতুন লেনদেন যুক্ত করতে '+ লেনদেন' বোতামে চাপুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Render Grouped List Sorted by Date
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedByDate.forEach { (_, dayTransactions) ->
                    val firstTx = dayTransactions.first()
                    val dayHeaderLabel = remember(firstTx.date) {
                        formatDateGroupHeader(firstTx.date)
                    }

                    // Day income vs expense mini totals
                    val dayIncome = dayTransactions.filter { it.isIncome }.sumOf { it.paidAmount }
                    val dayExpense = dayTransactions.filter { !it.isIncome }.sumOf { it.paidAmount }

                    if (showDateHeaders) {
                        // Date Group Header Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dayHeaderLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Day Totals Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (dayIncome > 0) {
                                    Text(
                                        text = "+৳${dayIncome.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CashInGreen
                                        )
                                    )
                                }
                                if (dayExpense > 0) {
                                    Text(
                                        text = "-৳${dayExpense.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CashOutRed
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // List items for this date with smooth slide-in animation
                    dayTransactions.forEachIndexed { index, tx ->
                        SlideInTransactionContainer(
                            itemIndex = index
                        ) {
                            RecentTransactionRowItem(
                                transaction = tx,
                                onClick = { onTransactionClick(tx) }
                            )
                        }
                    }
                }

                // Optional "সকল দেখুন (View All)" button if limited
                if (onViewAllClick != null && maxItems != null && transactions.size > maxItems) {
                    TextButton(
                        onClick = onViewAllClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "সকল ${transactions.size} টি লেনদেন দেখুন →",
                            style = MaterialTheme.typography.labelLarge.copy(
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

/**
 * Individual row item with prominent color-coded income vs expense visuals.
 */
@Composable
fun RecentTransactionRowItem(
    transaction: TransactionModel,
    onClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(transaction.date) { timeFormat.format(Date(transaction.date)) }

    val isIncome = transaction.isIncome
    val themeColor = if (isIncome) CashInGreen else CashOutRed

    // Icon differentiating Income from Expense
    val flowIcon: ImageVector = when (transaction.type) {
        TransactionType.CASH_IN -> Icons.Filled.ArrowDownward
        TransactionType.CUSTOMER_PAYMENT -> Icons.Filled.ArrowDownward
        TransactionType.CREDIT_SALE -> Icons.Filled.ArrowDownward
        TransactionType.CASH_OUT -> Icons.Filled.ArrowUpward
        TransactionType.SUPPLIER_PAYMENT -> Icons.Filled.ArrowUpward
        TransactionType.CREDIT_PURCHASE -> Icons.Filled.ArrowUpward
    }

    val partyName = transaction.customerName
        ?: transaction.supplierName
        ?: transaction.categoryOrSource.ifBlank { transaction.type.titleBn }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("recent_tx_item_${transaction.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Color-coded icon container + Title details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Color-Coded Icon Box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(themeColor.copy(alpha = 0.12f))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(themeColor.copy(alpha = 0.20f), themeColor.copy(alpha = 0.05f))
                            )
                        )
                ) {
                    Icon(
                        imageVector = flowIcon,
                        contentDescription = if (isIncome) "Income" else "Expense",
                        tint = themeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    // Party or Category Name + Flow Pill Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = partyName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        // Clear Income / Expense color-coded pill badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = themeColor.copy(alpha = 0.12f),
                            border = BorderStroke(0.8.dp, themeColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = if (isIncome) "আয়" else "ব্যয়",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColor
                                ),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Secondary info: Specific type, payment method, time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = transaction.type.titleBn,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = transaction.paymentMethod,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Signed Amount with Color Code and Due Badge
            Column(horizontalAlignment = Alignment.End) {
                val amountPrefix = if (isIncome) "+ " else "- "
                Text(
                    text = "$amountPrefix${LocaleStrings.formatTaka(transaction.amount.toInt())}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                )

                if (transaction.dueAmount > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DueOrange.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "বাকি: ${LocaleStrings.formatTaka(transaction.dueAmount.toInt())}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DueOrange
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Format a timestamp into a friendly date header like "আজ, ১৫ সেপ্টেম্বর", "গতকাল, ১৪ সেপ্টেম্বর".
 */
private fun formatDateGroupHeader(timestamp: Long): String {
    val targetCal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val todayCal = Calendar.getInstance()
    val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val isSameDay = { c1: Calendar, c2: Calendar ->
        c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    val dayMonthFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
    val dateString = dayMonthFormat.format(Date(timestamp))

    return when {
        isSameDay(targetCal, todayCal) -> "আজ ($dateString)"
        isSameDay(targetCal, yesterdayCal) -> "গতকাল ($dateString)"
        else -> dateString
    }
}
