package com.example.ui.screens.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
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
import com.example.ui.components.AmarKhataPrimaryButton
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItemCard(
    transaction: TransactionModel,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault())
    val isIncome = transaction.type == TransactionType.CASH_IN ||
            transaction.type == TransactionType.CUSTOMER_PAYMENT ||
            (transaction.type == TransactionType.CREDIT_SALE && transaction.paidAmount > 0)

    val icon: ImageVector
    val iconColor: Color
    val typeTitle: String

    when (transaction.type) {
        TransactionType.CASH_IN -> {
            icon = Icons.Filled.ArrowDownward
            iconColor = CashInGreen
            typeTitle = "টাকা পেলাম"
        }
        TransactionType.CASH_OUT -> {
            icon = Icons.Filled.ArrowUpward
            iconColor = CashOutRed
            typeTitle = "টাকা দিলাম"
        }
        TransactionType.CREDIT_SALE -> {
            icon = Icons.Filled.ShoppingCart
            iconColor = DueOrange
            typeTitle = "বাকিতে বিক্রি"
        }
        TransactionType.CREDIT_PURCHASE -> {
            icon = Icons.Filled.LocalShipping
            iconColor = Color(0xFF673AB7)
            typeTitle = "বাকিতে ক্রয়"
        }
        TransactionType.CUSTOMER_PAYMENT -> {
            icon = Icons.Filled.Payments
            iconColor = CashInGreen
            typeTitle = "কাস্টমার পেমেন্ট"
        }
        TransactionType.SUPPLIER_PAYMENT -> {
            icon = Icons.Filled.AttachMoney
            iconColor = CashOutRed
            typeTitle = "সাপ্লায়ার পেমেন্ট"
        }
    }

    val partyName = transaction.customerName ?: transaction.supplierName ?: transaction.categoryOrSource.ifBlank { typeTitle }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("transaction_item_${transaction.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = partyName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = iconColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = typeTitle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = iconColor
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (transaction.description.isNotBlank()) "${transaction.description} • ${dateFormat.format(Date(transaction.date))}"
                        else dateFormat.format(Date(transaction.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = LocaleStrings.formatTaka(transaction.amount.toInt()),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.type == TransactionType.CASH_OUT || transaction.type == TransactionType.SUPPLIER_PAYMENT) CashOutRed
                        else if (transaction.type == TransactionType.CREDIT_SALE) DueOrange
                        else CashInGreen
                    )
                )

                if (transaction.dueAmount > 0) {
                    Text(
                        text = "বাকি: ${LocaleStrings.formatTaka(transaction.dueAmount.toInt())}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = DueOrange
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsBottomSheet(
    sheetState: SheetState,
    transaction: TransactionModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale.getDefault())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "লেনদেনের বিস্তারিত",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Hero Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = transaction.type.titleBn,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocaleStrings.formatTaka(transaction.amount.toInt()),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    if (transaction.dueAmount > 0) {
                        Text(
                            text = "পরিশোধ: ${LocaleStrings.formatTaka(transaction.paidAmount.toInt())} | বকেয়া: ${LocaleStrings.formatTaka(transaction.dueAmount.toInt())}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = DueOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details List
            DetailRow(title = "তারিখ ও সময়", value = dateFormat.format(Date(transaction.date)))
            if (transaction.customerName != null) {
                DetailRow(title = "কাস্টমার", value = transaction.customerName)
            }
            if (transaction.supplierName != null) {
                DetailRow(title = "সাপ্লায়ার / মহাজন", value = transaction.supplierName)
            }
            if (transaction.categoryOrSource.isNotBlank()) {
                DetailRow(title = "খাত / উৎস", value = transaction.categoryOrSource)
            }
            DetailRow(title = "পেমেন্ট মেথড", value = transaction.paymentMethod)
            if (transaction.description.isNotBlank()) {
                DetailRow(title = "বিবরণ / নোট", value = transaction.description)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons (Edit & Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_delete_transaction")
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "মুছে ফেলুন")
                }

                AmarKhataPrimaryButton(
                    text = "সংশোধন (Edit)",
                    onClick = {
                        onDismiss()
                        onEdit()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_edit_transaction")
                )
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("লেনদেন মুছে ফেলতে চান?", fontWeight = FontWeight.Bold) },
            text = {
                Text("এই লেনদেনটি মুছে ফেললে কাস্টমার/সাপ্লায়ারের বাকি ব্যালেন্স এবং ক্যাশ বই স্বয়ংক্রিয়ভাবে পুনরায় হিসাব করা হবে।")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDismiss()
                        onDelete()
                    }
                ) {
                    Text("হ্যাঁ, মুছুন", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun DetailRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}
