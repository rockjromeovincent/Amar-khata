package com.example.models

enum class TransactionType(
    val id: String,
    val titleBn: String,
    val titleEn: String
) {
    CASH_IN("cash_in", "টাকা পেলাম", "Cash In"),
    CASH_OUT("cash_out", "টাকা দিলাম", "Cash Out"),
    CREDIT_SALE("credit_sale", "বাকিতে বিক্রি", "Credit Sale"),
    CREDIT_PURCHASE("credit_purchase", "বাকিতে ক্রয়", "Credit Purchase"),
    CUSTOMER_PAYMENT("customer_payment", "কাস্টমার থেকে টাকা পেলাম", "Customer Payment"),
    SUPPLIER_PAYMENT("supplier_payment", "সাপ্লায়ারকে টাকা দিলাম", "Supplier Payment");

    companion object {
        fun fromId(id: String): TransactionType {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: CASH_IN
        }
    }
}
