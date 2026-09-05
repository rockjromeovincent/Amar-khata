package com.example.models

data class LedgerEntry(
    val transactionId: String,
    val date: Long,
    val description: String,
    val type: TransactionType,
    val debitAmount: Double = 0.0,  // খরচ / বাকি বৃদ্ধি (e.g. বাকিতে বিক্রি)
    val creditAmount: Double = 0.0, // জমা / পেমেন্ট পরিশোধ (e.g. কাস্টমার পেমেন্ট)
    val runningBalance: Double = 0.0
)

data class DashboardSummary(
    val cashBalance: Double = 0.0,        // বর্তমান ব্যালেন্স (Cash Balance)
    val totalReceivable: Double = 0.0,    // মোট পাবেন (আমি পাব - Customer Dues)
    val totalPayable: Double = 0.0,       // মোট দেবেন (আমি দেব - Supplier Payables)
    val todayIncome: Double = 0.0,        // আজকের আয় / ক্যাশ ইন
    val todayExpense: Double = 0.0,       // আজকের খরচ / ক্যাশ আউট
    val todaySales: Double = 0.0,         // আজকের মোট বিক্রি (নগদ + বাকিতে)
    val todayTransactionCount: Int = 0
)
