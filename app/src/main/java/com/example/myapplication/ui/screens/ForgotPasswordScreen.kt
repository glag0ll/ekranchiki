package com.example.myapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.myapplication.ui.components.EnhancedTextField
import com.example.myapplication.ui.theme.Blue
import com.example.myapplication.ui.theme.LightTextSecondary
import kotlinx.coroutines.delay

private const val TAG = "ForgotPasswordScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val isEmailValid = remember(email) {
        email.contains("@") && email.isNotEmpty()
    }
    
    // Запрашиваем фокус при запуске экрана
    LaunchedEffect(Unit) {
        try {
            delay(300) // Небольшая задержка для лучшей работы
            focusRequester.requestFocus()
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting focus: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { 
                        try {
                            onNavigateBack()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error navigating back: ${e.message}")
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Забыл пароль",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Введите свою почту",
                fontSize = 14.sp,
                color = LightTextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            
            // Email icon and label for visual guidance
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon",
                tint = Blue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Email адрес",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            EnhancedTextField(
                value = email,
                onValueChange = { 
                    email = it 
                    emailError = if (!it.contains("@") && it.isNotEmpty()) {
                        "Некорректный формат email"
                    } else {
                        null
                    }
                },
                singleLine = true,
                isError = emailError != null,
                supportingText = { 
                    emailError?.let { Text(text = it) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isEmailValid) {
                            try {
                                keyboardController?.hide()
                                onSubmit(email)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error submitting form with keyboard action: ${e.message}")
                            }
                        } else {
                            emailError = "Некорректный формат email"
                        }
                    }
                ),
                autoFocus = true
            )

            Text(
                text = "Мы отправим инструкции по восстановлению пароля на указанный адрес",
                fontSize = 12.sp,
                color = LightTextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { 
                    if (isEmailValid) {
                        try {
                            keyboardController?.hide()
                            Log.d(TAG, "Submitting email: $email")
                            onSubmit(email)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error submitting form: ${e.message}")
                        }
                    } else {
                        emailError = "Некорректный формат email"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue,
                    contentColor = Color.White,
                    disabledContainerColor = Blue.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = isEmailValid
            ) {
                Text(
                    text = "Отправить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 