package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.screens.EmailVerificationScreen
import com.example.myapplication.ui.screens.ForgotPasswordScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.OtpVerificationScreen
import com.example.myapplication.ui.screens.RegisterScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.utils.KeyboardUtils
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Настраиваем окно для правильной работы с клавиатурой
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        
        // Пытаемся глобально отключить toolbar
        try {
            // Метод disableToolbar приватный, используем рефлексию для вызова
            val method = KeyboardUtils::class.java.getDeclaredMethod("disableToolbar", Context::class.java)
            method.isAccessible = true
            method.invoke(KeyboardUtils, this)
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling keyboard toolbar: ${e.message}")
        }
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Отслеживаем состояние клавиатуры
                val keyboardVisible by KeyboardUtils.keyboardAsState()
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusManager = LocalFocusManager.current
                
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Сбрасываем фокус и скрываем клавиатуру при навигации
                    DisposableEffect(navController) {
                        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
                            Log.d(TAG, "Navigation to: ${destination.route}")
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                        navController.addOnDestinationChangedListener(listener)
                        
                        onDispose {
                            navController.removeOnDestinationChangedListener(listener)
                        }
                    }
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onNavigateToRegister = {
                                    try {
                                        navController.navigate(Screen.Register.route)
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating to Register: ${e.message}")
                                    }
                                },
                                onNavigateToForgotPassword = {
                                    try {
                                        navController.navigate(Screen.ForgotPassword.route)
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating to ForgotPassword: ${e.message}")
                                    }
                                },
                                onLogin = { email, password ->
                                    // В реальном приложении здесь должна быть аутентификация
                                    // и навигация на главный экран после успешного входа
                                    Log.d(TAG, "Login attempt with email: $email")
                                }
                            )
                        }
                        
                        composable(Screen.Register.route) {
                            RegisterScreen(
                                onNavigateToLogin = {
                                    try {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating to Login from Register: ${e.message}")
                                    }
                                },
                                onRegister = { name, email, password, termsAccepted ->
                                    // В реальном приложении здесь должна быть регистрация
                                    // и возможно отправка письма для верификации
                                    try {
                                        val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                                        navController.navigate(Screen.EmailVerification.route + "/$encodedEmail")
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error during registration: ${e.message}")
                                        // Fallback navigation if encoding fails
                                        navController.navigate(Screen.EmailVerification.route + "/user_email")
                                    }
                                }
                            )
                        }
                        
                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                onNavigateBack = {
                                    try {
                                        Log.d(TAG, "Navigating back from ForgotPassword")
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating back from ForgotPassword: ${e.message}")
                                    }
                                },
                                onSubmit = { email ->
                                    try {
                                        Log.d(TAG, "Submitting forgot password email: $email")
                                        val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                                        navController.navigate(Screen.EmailVerification.route + "/$encodedEmail")
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating to EmailVerification from ForgotPassword: ${e.message}")
                                        // Fallback navigation if encoding fails
                                        navController.navigate(Screen.EmailVerification.route + "/user_email")
                                    }
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.EmailVerification.route + "/{email}",
                            arguments = listOf(
                                navArgument("email") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = "user_email"
                                }
                            )
                        ) { backStackEntry ->
                            val encodedEmail = backStackEntry.arguments?.getString("email") ?: "user_email"
                            val email = try {
                                URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
                            } catch (e: Exception) {
                                Log.e(TAG, "Error decoding email: ${e.message}")
                                // Возвращаем оригинальное значение в случае ошибки декодирования
                                encodedEmail
                            }
                            
                            Log.d(TAG, "Showing EmailVerification screen for email: $email")
                            
                            EmailVerificationScreen(
                                onNavigateBack = {
                                    try {
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating back from EmailVerification: ${e.message}")
                                    }
                                },
                                email = email,
                                onContinue = {
                                    try {
                                        navController.navigate(Screen.OtpVerification.route)
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating to OtpVerification: ${e.message}")
                                    }
                                }
                            )
                        }
                        
                        composable(Screen.OtpVerification.route) {
                            OtpVerificationScreen(
                                onNavigateBack = {
                                    try {
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating back from OtpVerification: ${e.message}")
                                    }
                                },
                                onVerify = { otp ->
                                    // В реальном приложении здесь должна быть проверка OTP
                                    // и навигация на главный экран или экран смены пароля
                                    try {
                                        Log.d(TAG, "OTP verification with code: $otp")
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error navigating to Login after OTP verification: ${e.message}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}