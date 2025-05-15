package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.EnhancedTextField
import com.example.myapplication.ui.theme.Blue
import com.example.myapplication.ui.theme.LightTextSecondary
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegister: (String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    
    // Состояние для ошибок
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Проверка валидности формы
    val isFormValid by remember {
        derivedStateOf {
            name.isNotEmpty() && name.length >= 2 &&
            email.isNotEmpty() && email.contains("@") && 
            password.isNotEmpty() && password.length >= 6 &&
            termsAccepted
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToLogin() }) {
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
                text = "Регистрация",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Заполните свои данные или продолжите через социальные медиа",
                fontSize = 14.sp,
                color = LightTextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Text(
                text = "Ваше имя",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            EnhancedTextField(
                value = name,
                onValueChange = { 
                    name = it 
                    nameError = if (it.length < 2 && it.isNotEmpty()) {
                        "Имя должно содержать не менее 2 символов"
                    } else {
                        null
                    }
                },
                singleLine = true,
                isError = nameError != null,
                supportingText = { 
                    nameError?.let { Text(text = it) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                autoFocus = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                fontSize = 14.sp,
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Пароль",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            EnhancedTextField(
                value = password,
                onValueChange = { 
                    password = it 
                    passwordError = if (it.length < 6 && it.isNotEmpty()) {
                        "Минимальная длина пароля 6 символов"
                    } else {
                        null
                    }
                },
                singleLine = true,
                isError = passwordError != null,
                supportingText = { 
                    passwordError?.let { Text(text = it) }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Даю согласие на обработку персональных данных",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    if (isFormValid) {
                        keyboardController?.hide()
                        onRegister(name, email, password, termsAccepted) 
                    } else {
                        if (name.length < 2) {
                            nameError = "Имя должно содержать не менее 2 символов"
                        } else if (!email.contains("@")) {
                            emailError = "Некорректный формат email"
                        } else if (password.length < 6) {
                            passwordError = "Минимальная длина пароля 6 символов"
                        }
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
                enabled = isFormValid
            ) {
                Text(
                    text = "Зарегистрироваться",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                TextButton(
                    onClick = { onNavigateToLogin() }
                ) {
                    Text(
                        text = "Есть аккаунт? Войти",
                        color = Blue,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
} 