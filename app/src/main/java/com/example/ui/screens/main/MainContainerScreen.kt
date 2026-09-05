package com.example.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.LocaleStrings
import com.example.ui.screens.customers.CustomersScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.transactions.TransactionsScreen
import com.example.viewmodels.AccountingViewModel
import com.example.viewmodels.AuthViewModel
import com.example.viewmodels.MainViewModel

data class NavItemData(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainContainerScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    accountingViewModel: AccountingViewModel,
    onLogoutSuccess: () -> Unit
) {
    val selectedTab by mainViewModel.selectedTab.collectAsState()

    val navItems = listOf(
        NavItemData(
            title = LocaleStrings.NAV_HOME,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_tab_home"
        ),
        NavItemData(
            title = LocaleStrings.NAV_TRANSACTIONS,
            selectedIcon = Icons.Filled.ReceiptLong,
            unselectedIcon = Icons.Outlined.ReceiptLong,
            testTag = "nav_tab_transactions"
        ),
        NavItemData(
            title = LocaleStrings.NAV_CUSTOMERS,
            selectedIcon = Icons.Filled.PeopleAlt,
            unselectedIcon = Icons.Outlined.PeopleAlt,
            testTag = "nav_tab_customers"
        ),
        NavItemData(
            title = LocaleStrings.NAV_REPORTS,
            selectedIcon = Icons.Filled.Assessment,
            unselectedIcon = Icons.Outlined.Assessment,
            testTag = "nav_tab_reports"
        ),
        NavItemData(
            title = LocaleStrings.NAV_MORE,
            selectedIcon = Icons.Filled.MoreHoriz,
            unselectedIcon = Icons.Outlined.MoreHoriz,
            testTag = "nav_tab_more"
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { mainViewModel.setSelectedTab(index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    mainViewModel = mainViewModel,
                    accountingViewModel = accountingViewModel,
                    onNavigateToTab = { mainViewModel.setSelectedTab(it) }
                )
                1 -> TransactionsScreen(
                    accountingViewModel = accountingViewModel
                )
                2 -> CustomersScreen(
                    accountingViewModel = accountingViewModel
                )
                3 -> ReportsScreen(
                    accountingViewModel = accountingViewModel
                )
                4 -> SettingsScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    onLogoutSuccess = onLogoutSuccess
                )
            }
        }
    }
}
