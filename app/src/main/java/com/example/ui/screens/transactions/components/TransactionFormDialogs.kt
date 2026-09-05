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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.models.CustomerModel
import com.example.models.SupplierModel
import com.example.models.TransactionModel
import com.example.models.TransactionType
import com.example.ui.components.AmarKhataPrimaryButton
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.DueOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val PAYMENT_METHODS = listOf(
    "নগদ (Cash)",
    "বিকাশ (bKash)",
    "নগদ (Nagad)",
    "রকেট (Rocket)",
    "ব্যাংক একাউন্ট"
)

val CASH_IN_SOURCES = listOf(
    "দৈনিক বিক্রি",
    "সার্ভিস আয়",
    "মূলধন বিনিয়োগ",
    "কমিশন আয়",
    "অন্যান্য আয়"
)

val CASH_OUT_CATEGORIES = listOf(
    "দোকান ভাড়া",
    "বিদ্যুৎ ও ইউটিলিটি বিল",
    "পরিবহন খরচ",
    "স্টাফ বেতন",
    "চা-নাস্তা ও আপ্যায়ন",
    "অন্যান্য দোকান খরচ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormBottomSheet(
    sheetState: SheetState,
    initialType: TransactionType,
    existingTransaction: TransactionModel? = null,
    customers: List<CustomerModel>,
    suppliers: List<SupplierModel>,
    onDismiss: () -> Unit,
    onQuickAddCustomer: () -> Unit,
    onQuickAddSupplier: () -> Unit,
    onSaveCashIn: (amount: Double, source: String, date: Long, note: String, method: String) -> Unit,
    onSaveCashOut: (amount: Double, category: String, date: Long, note: String, method: String) -> Unit,
    onSaveCreditSale: (customerId: String, customerName: String, amount: Double, paid: Double, date: Long, note: String, method: String) -> Unit,
    onSaveCreditPurchase: (supplierId: String, supplierName: String, amount: Double, paid: Double, date: Long, note: String, method: String) -> Unit,
    onSaveCustomerPayment: (customerId: String, customerName: String, amount: Double, date: Long, note: String, method: String) -> Unit,
    onSaveSupplierPayment: (supplierId: String, supplierName: String, amount: Double, date: Long, note: String, method: String) -> Unit,
    onUpdateTransaction: (TransactionModel) -> Unit = {}
) {
    val isEditing = existingTransaction != null
    val type = existingTransaction?.type ?: initialType

    var amountText by remember {
        mutableStateOf(existingTransaction?.amount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var paidText by remember {
        mutableStateOf(existingTransaction?.paidAmount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var selectedCustomerId by remember { mutableStateOf(existingTransaction?.customerId ?: customers.firstOrNull()?.id ?: "") }
    var selectedCustomerName by remember {
        mutableStateOf(existingTransaction?.customerName ?: customers.firstOrNull()?.name ?: "")
    }
    var selectedSupplierId by remember { mutableStateOf(existingTransaction?.supplierId ?: suppliers.firstOrNull()?.id ?: "") }
    var selectedSupplierName by remember {
        mutableStateOf(existingTransaction?.supplierName ?: suppliers.firstOrNull()?.name ?: "")
    }
    var categoryOrSource by remember {
        mutableStateOf(existingTransaction?.categoryOrSource ?: if (type == TransactionType.CASH_IN) CASH_IN_SOURCES.first() else if (type == TransactionType.CASH_OUT) CASH_OUT_CATEGORIES.first() else "")
    }
    var note by remember { mutableStateOf(existingTransaction?.description ?: "") }
    var selectedPaymentMethod by remember { mutableStateOf(existingTransaction?.paymentMethod ?: PAYMENT_METHODS.first()) }
    var dateTimestamp by remember { mutableStateOf(existingTransaction?.date ?: System.currentTimeMillis()) }

    var showCustomerMenu by remember { mutableStateOf(false) }
    var showSupplierMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showPaymentMethodMenu by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val paid = if (type == TransactionType.CREDIT_SALE || type == TransactionType.CREDIT_PURCHASE) {
        paidText.toDoubleOrNull() ?: 0.0
    } else {
        amount
    }
    val calculatedDue = (amount - paid).coerceAtLeast(0.0)

    val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEditing) "লেনদেন সংশোধন করুন" else type.titleBn,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (type) {
                            TransactionType.CASH_IN -> "টাকা গ্রহণ বা আয়ের তথ্য দিন"
                            TransactionType.CASH_OUT -> "টাকা প্রদান বা খরচের তথ্য দিন"
                            TransactionType.CREDIT_SALE -> "কাস্টমারের বাকিতে বিক্রির হিসাব"
                            TransactionType.CREDIT_PURCHASE -> "মহাজন বা সাপ্লায়ারের বাকিতে ক্রয়ের হিসাব"
                            TransactionType.CUSTOMER_PAYMENT -> "কাস্টমারের পূর্বের বকেয়া আদায়"
                            TransactionType.SUPPLIER_PAYMENT -> "সাপ্লায়ারের পূর্বের পাওনা পরিশোধ"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Amount Field (Primary Hero Field)
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                label = {
                    Text(
                        text = when (type) {
                            TransactionType.CASH_IN -> "আয়ের পরিমাণ (টাকা) *"
                            TransactionType.CASH_OUT -> "ব্যয়ের পরিমাণ (টাকা) *"
                            TransactionType.CREDIT_SALE -> "মোট বিক্রির পরিমাণ (টাকা) *"
                            TransactionType.CREDIT_PURCHASE -> "মোট ক্রয়ের পরিমাণ (টাকা) *"
                            TransactionType.CUSTOMER_PAYMENT -> "আদায়কৃত টাকার পরিমাণ *"
                            TransactionType.SUPPLIER_PAYMENT -> "পরিশোধকৃত টাকার পরিমাণ *"
                        }
                    )
                },
                leadingIcon = {
                    Text(
                        text = "৳",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                placeholder = { Text("০.০০") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_amount")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Customer / Supplier Selection (if applicable)
            if (type == TransactionType.CREDIT_SALE || type == TransactionType.CUSTOMER_PAYMENT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "কাস্টমার নির্বাচন করুন *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onQuickAddCustomer) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "নতুন কাস্টমার", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (selectedCustomerName.isNotBlank()) selectedCustomerName else "কাস্টমার নির্বাচন করুন",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showCustomerMenu = true }) {
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCustomerMenu = true }
                            .testTag("select_customer_field")
                    )

                    DropdownMenu(
                        expanded = showCustomerMenu,
                        onDismissRequest = { showCustomerMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (customers.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("কোনো কাস্টমার পাওয়া যায়নি। নতুন যোগ করুন") },
                                onClick = {
                                    showCustomerMenu = false
                                    onQuickAddCustomer()
                                }
                            )
                        } else {
                            customers.forEach { cust ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = cust.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "${cust.phone} • বর্তমান বাকি: ${LocaleStrings.formatTaka(cust.totalDue.toInt())}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedCustomerId = cust.id
                                        selectedCustomerName = cust.name
                                        showCustomerMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else if (type == TransactionType.CREDIT_PURCHASE || type == TransactionType.SUPPLIER_PAYMENT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "সাপ্লায়ার / মহাজন নির্বাচন করুন *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onQuickAddSupplier) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "নতুন সাপ্লায়ার", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (selectedSupplierName.isNotBlank()) selectedSupplierName else "সাপ্লায়ার নির্বাচন করুন",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showSupplierMenu = true }) {
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSupplierMenu = true }
                            .testTag("select_supplier_field")
                    )

                    DropdownMenu(
                        expanded = showSupplierMenu,
                        onDismissRequest = { showSupplierMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (suppliers.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("কোনো সাপ্লায়ার পাওয়া যায়নি। নতুন যোগ করুন") },
                                onClick = {
                                    showSupplierMenu = false
                                    onQuickAddSupplier()
                                }
                            )
                        } else {
                            suppliers.forEach { supp ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = supp.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "${supp.phone} • দেনা: ${LocaleStrings.formatTaka(supp.totalPayable.toInt())}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedSupplierId = supp.id
                                        selectedSupplierName = supp.name
                                        showSupplierMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 3. Paid & Auto-calculated Due for Credit Sale & Credit Purchase
            if (type == TransactionType.CREDIT_SALE || type == TransactionType.CREDIT_PURCHASE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = paidText,
                        onValueChange = { paidText = it },
                        label = { Text("জমা / নগদ পরিশোধ") },
                        placeholder = { Text("০") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_transaction_paid")
                    )

                    // Auto-calculated Due Display Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DueOrange.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "বাকি থাকবে (Due)",
                                style = MaterialTheme.typography.labelSmall,
                                color = DueOrange
                            )
                            Text(
                                text = LocaleStrings.formatTaka(calculatedDue.toInt()),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DueOrange
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 4. Category or Source Dropdown for Cash In / Out
            if (type == TransactionType.CASH_IN || type == TransactionType.CASH_OUT) {
                val list = if (type == TransactionType.CASH_IN) CASH_IN_SOURCES else CASH_OUT_CATEGORIES
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categoryOrSource.ifBlank { list.first() },
                        onValueChange = { categoryOrSource = it },
                        label = { Text(if (type == TransactionType.CASH_IN) "আয়ের উৎস (Source)" else "ব্যয়ের খাত (Category)") },
                        trailingIcon = {
                            IconButton(onClick = { showCategoryMenu = true }) {
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        list.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoryOrSource = cat
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 5. Payment Method Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedPaymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("পেমেন্ট মেথড (মাধ্যম)") },
                    trailingIcon = {
                        IconButton(onClick = { showPaymentMethodMenu = true }) {
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = showPaymentMethodMenu,
                    onDismissRequest = { showPaymentMethodMenu = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    PAYMENT_METHODS.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                selectedPaymentMethod = method
                                showPaymentMethodMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 6. Note / Description Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("বিবরণ / নোট (ঐচ্ছিক)") },
                placeholder = { Text("যেমন: মালের বিবরণ বা স্লিপ নম্বর") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 7. Date indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "তারিখ: ${dateFormat.format(Date(dateTimestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save / Submit Button
            AmarKhataPrimaryButton(
                text = if (isEditing) "আপডেট সংরক্ষণ করুন" else "লেনদেন নিশ্চিত করুন",
                onClick = {
                    if (amount <= 0.0) {
                        errorMessage = "অনুগ্রহ করে সঠিক টাকার পরিমাণ লিখুন"
                        return@AmarKhataPrimaryButton
                    }

                    if (isEditing && existingTransaction != null) {
                        val updated = existingTransaction.copy(
                            amount = amount,
                            paidAmount = paid,
                            dueAmount = calculatedDue,
                            customerId = if (type == TransactionType.CREDIT_SALE || type == TransactionType.CUSTOMER_PAYMENT) selectedCustomerId else null,
                            customerName = if (type == TransactionType.CREDIT_SALE || type == TransactionType.CUSTOMER_PAYMENT) selectedCustomerName else null,
                            supplierId = if (type == TransactionType.CREDIT_PURCHASE || type == TransactionType.SUPPLIER_PAYMENT) selectedSupplierId else null,
                            supplierName = if (type == TransactionType.CREDIT_PURCHASE || type == TransactionType.SUPPLIER_PAYMENT) selectedSupplierName else null,
                            categoryOrSource = categoryOrSource,
                            description = note,
                            paymentMethod = selectedPaymentMethod,
                            updatedAt = System.currentTimeMillis()
                        )
                        onUpdateTransaction(updated)
                        onDismiss()
                        return@AmarKhataPrimaryButton
                    }

                    when (type) {
                        TransactionType.CASH_IN -> {
                            onSaveCashIn(amount, categoryOrSource, dateTimestamp, note, selectedPaymentMethod)
                        }
                        TransactionType.CASH_OUT -> {
                            onSaveCashOut(amount, categoryOrSource, dateTimestamp, note, selectedPaymentMethod)
                        }
                        TransactionType.CREDIT_SALE -> {
                            if (selectedCustomerId.isBlank()) {
                                errorMessage = "অনুগ্রহ করে কাস্টমার নির্বাচন করুন"
                                return@AmarKhataPrimaryButton
                            }
                            onSaveCreditSale(selectedCustomerId, selectedCustomerName, amount, paid, dateTimestamp, note, selectedPaymentMethod)
                        }
                        TransactionType.CREDIT_PURCHASE -> {
                            if (selectedSupplierId.isBlank()) {
                                errorMessage = "অনুগ্রহ করে সাপ্লায়ার নির্বাচন করুন"
                                return@AmarKhataPrimaryButton
                            }
                            onSaveCreditPurchase(selectedSupplierId, selectedSupplierName, amount, paid, dateTimestamp, note, selectedPaymentMethod)
                        }
                        TransactionType.CUSTOMER_PAYMENT -> {
                            if (selectedCustomerId.isBlank()) {
                                errorMessage = "অনুগ্রহ করে কাস্টমার নির্বাচন করুন"
                                return@AmarKhataPrimaryButton
                            }
                            onSaveCustomerPayment(selectedCustomerId, selectedCustomerName, amount, dateTimestamp, note, selectedPaymentMethod)
                        }
                        TransactionType.SUPPLIER_PAYMENT -> {
                            if (selectedSupplierId.isBlank()) {
                                errorMessage = "অনুগ্রহ করে সাপ্লায়ার নির্বাচন করুন"
                                return@AmarKhataPrimaryButton
                            }
                            onSaveSupplierPayment(selectedSupplierId, selectedSupplierName, amount, dateTimestamp, note, selectedPaymentMethod)
                        }
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_submit_transaction")
            )
        }
    }
}
