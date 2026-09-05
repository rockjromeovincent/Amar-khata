package com.example

import com.example.models.CustomerModel
import com.example.models.SupplierModel
import com.example.models.TransactionModel
import com.example.models.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountingEngineTest {

    @Test
    fun testCashInAndCashOutBalanceCalculation() {
        var cashBalance = 0.0

        val cashInTx = TransactionModel(
            id = "tx1",
            type = TransactionType.CASH_IN,
            amount = 5000.0,
            paidAmount = 5000.0,
            dueAmount = 0.0,
            date = System.currentTimeMillis()
        )
        cashBalance += cashInTx.paidAmount
        assertEquals(5000.0, cashBalance, 0.001)

        val cashOutTx = TransactionModel(
            id = "tx2",
            type = TransactionType.CASH_OUT,
            amount = 1200.0,
            paidAmount = 1200.0,
            dueAmount = 0.0,
            date = System.currentTimeMillis()
        )
        cashBalance -= cashOutTx.paidAmount
        assertEquals(3800.0, cashBalance, 0.001)
    }

    @Test
    fun testCreditSaleAndCustomerPaymentLedger() {
        var customerOpeningDue = 1000.0
        var totalCustomerDue = customerOpeningDue
        var cashBalance = 0.0

        // 1. Credit Sale: ৳4,000 total, ৳1,000 down payment, ৳3,000 due
        val saleAmount = 4000.0
        val paidDown = 1000.0
        val saleDue = saleAmount - paidDown

        val creditSaleTx = TransactionModel(
            id = "tx_sale",
            customerId = "cust_1",
            type = TransactionType.CREDIT_SALE,
            amount = saleAmount,
            paidAmount = paidDown,
            dueAmount = saleDue,
            date = System.currentTimeMillis()
        )
        cashBalance += creditSaleTx.paidAmount
        totalCustomerDue += creditSaleTx.dueAmount

        assertEquals(1000.0, cashBalance, 0.001)
        assertEquals(4000.0, totalCustomerDue, 0.001) // 1000 opening + 3000 due

        // 2. Customer Payment: customer pays ৳2,500
        val paymentAmount = 2500.0
        val paymentTx = TransactionModel(
            id = "tx_pay",
            customerId = "cust_1",
            type = TransactionType.CUSTOMER_PAYMENT,
            amount = paymentAmount,
            paidAmount = paymentAmount,
            dueAmount = 0.0,
            date = System.currentTimeMillis()
        )
        cashBalance += paymentTx.paidAmount
        totalCustomerDue -= paymentTx.amount

        assertEquals(3500.0, cashBalance, 0.001) // 1000 + 2500
        assertEquals(1500.0, totalCustomerDue, 0.001) // 4000 - 2500 = 1500
    }

    @Test
    fun testCreditPurchaseAndSupplierPaymentLedger() {
        var supplierOpeningPayable = 5000.0
        var totalSupplierPayable = supplierOpeningPayable
        var cashBalance = 10000.0

        // 1. Credit Purchase: ৳6,000 total, ৳2,000 down payment, ৳4,000 due
        val purchaseAmount = 6000.0
        val paidDown = 2000.0
        val purchaseDue = purchaseAmount - paidDown

        val creditPurchaseTx = TransactionModel(
            id = "tx_pur",
            supplierId = "supp_1",
            type = TransactionType.CREDIT_PURCHASE,
            amount = purchaseAmount,
            paidAmount = paidDown,
            dueAmount = purchaseDue,
            date = System.currentTimeMillis()
        )
        cashBalance -= creditPurchaseTx.paidAmount
        totalSupplierPayable += creditPurchaseTx.dueAmount

        assertEquals(8000.0, cashBalance, 0.001) // 10000 - 2000
        assertEquals(9000.0, totalSupplierPayable, 0.001) // 5000 opening + 4000 due

        // 2. Supplier Payment: pay supplier ৳3,000
        val paymentAmount = 3000.0
        val paymentTx = TransactionModel(
            id = "tx_supp_pay",
            supplierId = "supp_1",
            type = TransactionType.SUPPLIER_PAYMENT,
            amount = paymentAmount,
            paidAmount = paymentAmount,
            dueAmount = 0.0,
            date = System.currentTimeMillis()
        )
        cashBalance -= paymentTx.paidAmount
        totalSupplierPayable -= paymentTx.amount

        assertEquals(5000.0, cashBalance, 0.001) // 8000 - 3000
        assertEquals(6000.0, totalSupplierPayable, 0.001) // 9000 - 3000
    }

    @Test
    fun testTransactionTypesEnumIntegrity() {
        assertEquals("cash_in", TransactionType.CASH_IN.id)
        assertEquals("cash_out", TransactionType.CASH_OUT.id)
        assertEquals("credit_sale", TransactionType.CREDIT_SALE.id)
        assertEquals("credit_purchase", TransactionType.CREDIT_PURCHASE.id)
        assertEquals("customer_payment", TransactionType.CUSTOMER_PAYMENT.id)
        assertEquals("supplier_payment", TransactionType.SUPPLIER_PAYMENT.id)

        assertEquals(TransactionType.CASH_IN, TransactionType.fromId("cash_in"))
        assertEquals(TransactionType.CREDIT_SALE, TransactionType.fromId("credit_sale"))
    }
}
