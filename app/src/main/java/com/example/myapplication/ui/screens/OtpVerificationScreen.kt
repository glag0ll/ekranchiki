package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.Blue
import com.example.myapplication.ui.theme.LightSurface
import com.example.myapplication.ui.theme.LightTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun OtpVerificationScreen(
    onNavigateBack: () -> Unit,
    onVerify: (String) -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var timeLeft by remember { mutableIntStateOf(30) }
    var canResend by remember { mutableStateOf(false) }
    var resendKey by remember { mutableIntStateOf(0) } // Ключ для повторного старта таймера
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var isFocused by remember { mutableStateOf(false) }
    
    // Запрашиваем фокус и показываем клавиатуру при запуске экрана
    LaunchedEffect(Unit) {
        delay(500) // Увеличенная задержка для гарантированного отображения клавиатуры
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    
    // Обратный отсчет для повторной отправки
    LaunchedEffect(resendKey) {
        timeLeft = 30
        canResend = false
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        canResend = true
    }
    
    // Автоматическая отправка OTP при вводе всех 5 цифр
    LaunchedEffect(otpValue) {
        if (otpValue.length == 5) {
            delay(300) // Небольшая задержка для UX
            keyboardController?.hide()
            onVerify(otpValue)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
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
                text = "OTP проверка",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Пожалуйста проверьте свою электронную почту, чтобы увидеть код подтверждения",
                fontSize = 14.sp,
                color = LightTextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            Text(
                text = "OTP код",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = if (isFocused) Blue else Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 24.dp)
                    .onFocusChanged { isFocused = it.isFocused }
                    .focusRequester(focusRequester),
                contentAlignment = Alignment.Center
            ) {
                // Отображаем OTP поля
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(5) { index ->
                        OtpDigitBox(
                            value = otpValue.getOrNull(index)?.toString() ?: "",
                            isFocused = otpValue.length == index && isFocused
                        )
                    }
                }
                
                // Скрытое текстовое поле для ввода
                BasicTextField(
                    value = TextFieldValue(otpValue, selection = TextRange(otpValue.length)),
                    onValueChange = {
                        if (it.text.length <= 5 && it.text.all { char -> char.isDigit() }) {
                            otpValue = it.text
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (otpValue.length == 5) {
                                keyboardController?.hide()
                                onVerify(otpValue)
                            }
                        }
                    ),
                    textStyle = TextStyle(color = Color.Transparent), // Делаем текст невидимым
                    cursorBrush = SolidColor(Color.Transparent) // Скрываем курсор
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canResend) {
                    TextButton(
                        onClick = {
                            // Переинициализация для повторной отправки
                            otpValue = ""
                            resendKey++ // Инкрементируем ключ для повторного запуска LaunchedEffect
                            coroutineScope.launch {
                                delay(300) // Небольшая задержка для лучшей работы
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        }
                    ) {
                        Text(
                            text = "Отправить код заново",
                            color = Blue
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(0.dp)) // Placeholder
                }
                
                Text(
                    text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                    fontSize = 12.sp,
                    color = LightTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    keyboardController?.hide()
                    onVerify(otpValue) 
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
                enabled = otpValue.length == 5
            ) {
                Text(
                    text = "Подтвердить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun OtpDigitBox(
    value: String,
    isFocused: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (value.isNotEmpty()) Color.White else LightSurface)
            .border(
                width = if (value.isNotEmpty()) 2.dp else 1.dp,
                color = when {
                    isFocused -> Blue
                    value.isNotEmpty() -> Blue.copy(alpha = 0.5f)
                    else -> Color.Gray.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .padding(2.dp)
    ) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (value.isNotEmpty()) Color.Black else Color.Gray.copy(alpha = 0.5f)
        )
    }
} 