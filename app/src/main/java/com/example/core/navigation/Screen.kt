package com.example.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object MainContainer : Screen("main_container")
}

sealed class BottomNavItem(
    val route: String,
    val titleBn: String,
    val titleEn: String
) {
    object Home : BottomNavItem("nav_home", "হোম", "Home")
    object Transactions : BottomNavItem("nav_transactions", "লেনদেন", "Transactions")
    object Customers : BottomNavItem("nav_customers", "কাস্টমার", "Customers")
    object Reports : BottomNavItem("nav_reports", "রিপোর্ট", "Reports")
    object More : BottomNavItem("nav_more", "আরও", "More")

    companion object {
        val items = listOf(Home, Transactions, Customers, Reports, More)
    }
}
