package com.example.myapplication.ui.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Вспомогательные функции для работы с клавиатурой
 */
object KeyboardUtils {
    
    /**
     * Composable функция, которая отслеживает состояние клавиатуры (показана/скрыта)
     * и возвращает State<Boolean> с этим состоянием
     */
    @Composable
    fun keyboardAsState(): State<Boolean> {
        val keyboardState = remember { mutableStateOf(false) }
        val view = LocalView.current
        
        DisposableEffect(view) {
            val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
                val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
                keyboardState.value = isKeyboardOpen
            }
            
            view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
            
            onDispose {
                view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
            }
        }
        
        return keyboardState
    }
    
    /**
     * Принудительно показывает клавиатуру для указанного вью без toolbar
     */
    fun showKeyboardWithoutToolbar(view: View) {
        val context = view.context
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        
        // Сначала пытаемся отключить toolbar системным способом
        disableToolbar(context)
        
        // Затем показываем клавиатуру
        Handler(Looper.getMainLooper()).postDelayed({
            view.requestFocus()
            imm?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }, 100)
    }
    
    /**
     * Пытается отключить toolbar системным способом (может работать не на всех устройствах)
     */
    private fun disableToolbar(context: Context) {
        try {
            // На Android 11+ можно использовать системную настройку
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Settings.Secure.putInt(
                    context.contentResolver,
                    "show_ime_with_hard_keyboard",
                    0
                )
            }
            
            // Для других версий Android можем попробовать через InputMethodManager
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val methodName = "disableAccessoryView"
            
            try {
                val method = InputMethodManager::class.java.getMethod(methodName)
                method.isAccessible = true
                method.invoke(imm)
            } catch (e: Exception) {
                // Не все устройства поддерживают эту функцию
            }
        } catch (e: Exception) {
            // Если ничего не получилось, просто продолжаем
        }
    }
} 