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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.TransactionType
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTransactionBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectType: (TransactionType) -> Unit
) {
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
                .padding(bottom = 36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "+ নতুন লেনদেন এন্ট্রি",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "লেনদেনের ধরন নির্বাচন করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6 Quick Options Grid (2 columns x 3 rows)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickOptionCard(
                        title = "টাকা পেলাম",
                        subtitle = "নগদ আয় / ক্যাশ ইন",
                        icon = Icons.Filled.ArrowDownward,
                        color = CashInGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_cash_in",
                        onClick = { onSelectType(TransactionType.CASH_IN) }
                    )

                    QuickOptionCard(
                        title = "টাকা দিলাম",
                        subtitle = "নগদ ব্যয় / ক্যাশ আউট",
                        icon = Icons.Filled.ArrowUpward,
                        color = CashOutRed,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_cash_out",
                        onClick = { onSelectType(TransactionType.CASH_OUT) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickOptionCard(
                        title = "বাকিতে বিক্রি",
                        subtitle = "কাস্টমার বাকি বৃদ্ধি",
                        icon = Icons.Filled.ShoppingCart,
                        color = DueOrange,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_credit_sale",
                        onClick = { onSelectType(TransactionType.CREDIT_SALE) }
                    )

                    QuickOptionCard(
                        title = "বাকিতে ক্রয়",
                        subtitle = "সাপ্লায়ার দেনা বৃদ্ধি",
                        icon = Icons.Filled.LocalShipping,
                        color = Color(0xFF673AB7),
                        modifier = Modifier.weight(1f),
                        testTag = "quick_credit_purchase",
                        onClick = { onSelectType(TransactionType.CREDIT_PURCHASE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickOptionCard(
                        title = "কাস্টমার থেকে পেলাম",
                        subtitle = "বকেয়া টাকা আদায়",
                        icon = Icons.Filled.Payments,
                        color = CashInGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_customer_payment",
                        onClick = { onSelectType(TransactionType.CUSTOMER_PAYMENT) }
                    )

                    QuickOptionCard(
                        title = "সাপ্লায়ারকে দিলাম",
                        subtitle = "মহাজনের পাওনা পরিশোধ",
                        icon = Icons.Filled.AttachMoney,
                        color = CashOutRed,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_supplier_payment",
                        onClick = { onSelectType(TransactionType.SUPPLIER_PAYMENT) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
