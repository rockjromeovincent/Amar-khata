package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.Screen
import com.example.repositories.AuthRepository
import com.example.repositories.UserPreferencesRepository
import com.example.services.FirebaseAuthService
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.main.MainContainerScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.AmarKhataTheme
import com.example.viewmodels.AuthViewModel
import com.example.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appDatabase = com.example.core.database.AppDatabase.getDatabase(applicationContext)
        val firebaseAuthService = FirebaseAuthService()
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)
        val authRepository = AuthRepository(firebaseAuthService, userPreferencesRepository, appDatabase.userDao())
        val accountingRepository = com.example.repositories.AccountingRepository(appDatabase)

        setContent {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(userPreferencesRepository, appDatabase.userDao())
            )
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(authRepository)
            )
            val accountingViewModel: com.example.viewmodels.AccountingViewModel = viewModel(
                factory = com.example.viewmodels.AccountingViewModel.Factory(accountingRepository)
            )

            val currentThemeMode by mainViewModel.themeMode.collectAsState()

            AmarKhataTheme(appThemeMode = currentThemeMode) {
                AmarKhataApp(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    accountingViewModel = accountingViewModel
                )
            }
        }
    }
}

@Composable
fun AmarKhataApp(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    accountingViewModel: com.example.viewmodels.AccountingViewModel
) {
    val navController = rememberNavController()
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
    val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Splash Screen
        composable(
            route = Screen.Splash.route,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                )
            }
        ) {
            SplashScreen(
                isLoggedIn = isLoggedIn,
                isOnboardingCompleted = isOnboardingCompleted,
                onNavigateToHome = {
                    navController.navigate(Screen.MainContainer.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Onboarding Screen
        composable(
            route = Screen.Onboarding.route,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                )
            }
        ) {
            OnboardingScreen(
                onFinishOnboarding = {
                    mainViewModel.setOnboardingCompleted()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Login Screen
        composable(
            route = Screen.Login.route,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                )
            }
        ) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    authViewModel.clearMessages()
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    authViewModel.clearMessages()
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.MainContainer.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Registration Screen
        composable(
            route = Screen.Register.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                )
            }
        ) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.MainContainer.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 5. Forgot Password Screen
        composable(
            route = Screen.ForgotPassword.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                )
            }
        ) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                }
            )
        }

        // 6. Main Dashboard & Container Screen (5 Tabs)
        composable(
            route = Screen.MainContainer.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(400)
                )
            }
        ) {
            MainContainerScreen(
                mainViewModel = mainViewModel,
                authViewModel = authViewModel,
                accountingViewModel = accountingViewModel,
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MainContainer.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
