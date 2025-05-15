package com.example.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.delay
import com.example.myapplication.ui.theme.Blue
import com.example.myapplication.ui.theme.LightSurface
import com.example.myapplication.ui.utils.KeyboardUtils

/**
 * Улучшенный компонент текстового поля для решения проблем с клавиатурой
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    singleLine: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = LightSurface,
        unfocusedContainerColor = LightSurface,
        disabledContainerColor = LightSurface,
        cursorColor = Blue,
        focusedIndicatorColor = Blue,
        focusedLabelColor = Blue,
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    autoFocus: Boolean = false,
    focusDelay: Long = 300
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isPressed = interactionSource.collectIsPressedAsState()
    val isFocused = interactionSource.collectIsFocusedAsState()
    val context = LocalContext.current
    val view = LocalView.current
    
    // Animate the container color based on focus state
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused.value) Color.White else LightSurface,
        animationSpec = tween(durationMillis = 300),
        label = "background"
    )

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            delay(focusDelay)
            focusRequester.requestFocus()
            delay(100)
            KeyboardUtils.showKeyboardWithoutToolbar(view)
        }
    }
    
    // Дополнительно проверяем, показываем клавиатуру при нажатии
    LaunchedEffect(isPressed.value) {
        if (isPressed.value) {
            delay(50)
            KeyboardUtils.showKeyboardWithoutToolbar(view)
        }
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                if (state.isFocused) {
                    KeyboardUtils.showKeyboardWithoutToolbar(view)
                }
            },
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        isError = isError,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions.copy(
            imeAction = keyboardOptions.imeAction,
            keyboardType = keyboardOptions.keyboardType,
            capitalization = keyboardOptions.capitalization
        ),
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        colors = colors,
        interactionSource = interactionSource
    )
} 