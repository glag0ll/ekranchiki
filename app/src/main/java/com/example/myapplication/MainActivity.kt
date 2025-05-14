package com.example.myapplication

import android.os.Bundle
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
            // Если не сработало, ничего страшного, будем отключать toolbar для каждого поля отдельно
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
                        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
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
                                    navController.navigate(Screen.Register.route)
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate(Screen.ForgotPassword.route)
                                },
                                onLogin = { email, password ->
                                    // В реальном приложении здесь должна быть аутентификация
                                    // и навигация на главный экран после успешного входа
                                }
                            )
                        }
                        
                        composable(Screen.Register.route) {
                            RegisterScreen(
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onRegister = { name, email, password, termsAccepted ->
                                    // В реальном приложении здесь должна быть регистрация
                                    // и возможно отправка письма для верификации
                                    val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                                    navController.navigate(Screen.EmailVerification.route + "/$encodedEmail")
                                }
                            )
                        }
                        
                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onSubmit = { email ->
                                    val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                                    navController.navigate(Screen.EmailVerification.route + "/$encodedEmail")
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.EmailVerification.route + "/{email}",
                            arguments = listOf(
                                navArgument("email") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
                            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
                            
                            EmailVerificationScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                email = email,
                                onContinue = {
                                    navController.navigate(Screen.OtpVerification.route)
                                }
                            )
                        }
                        
                        composable(Screen.OtpVerification.route) {
                            OtpVerificationScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onVerify = { otp ->
                                    // В реальном приложении здесь должна быть проверка OTP
                                    // и навигация на главный экран или экран смены пароля
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
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