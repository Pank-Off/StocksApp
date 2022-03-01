package ru.punkoff.stocksapp.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ChartFragmentBinding
import java.io.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@SuppressLint("ClickableViewAccessibility")
fun TextInputEditText.onLeftDrawableClicked(onClicked: (view: TextInputEditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is TextInputEditText) {
            if (event.x <= v.totalPaddingLeft) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun MaterialButton.activateButton(binding: ChartFragmentBinding) {
    with(binding) {
        when (this@activateButton.id) {
            R.id.day_btn -> {
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                dayBtn.setTextColor(Color.WHITE)
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.card_background)
                weekBtn.setTextColor(Color.BLACK)
                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(monthBtn.context, R.color.card_background)
                monthBtn.setTextColor(Color.BLACK)
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                yearBtn.setTextColor(Color.BLACK)
            }
            R.id.week_btn -> {
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                weekBtn.setTextColor(Color.WHITE)
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(dayBtn.context, R.color.card_background)
                dayBtn.setTextColor(Color.BLACK)
                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(monthBtn.context, R.color.card_background)
                monthBtn.setTextColor(Color.BLACK)
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                yearBtn.setTextColor(Color.BLACK)
            }
            R.id.month_btn -> {
                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                monthBtn.setTextColor(Color.WHITE)
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.card_background)
                weekBtn.setTextColor(Color.BLACK)
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(dayBtn.context, R.color.card_background)
                dayBtn.setTextColor(Color.BLACK)
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                yearBtn.setTextColor(Color.BLACK)
            }
            R.id.year_btn -> {
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.black)
                yearBtn.setTextColor(Color.WHITE)
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.card_background)
                weekBtn.setTextColor(Color.BLACK)

                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(monthBtn.context, R.color.card_background)
                monthBtn.setTextColor(Color.BLACK)
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                dayBtn.setTextColor(Color.BLACK)
            }
        }
    }
}

fun LifecycleOwner.addRepeatingJob(
    state: Lifecycle.State,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(coroutineContext) {
    repeatOnLifecycle(state, block)
}

fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        is Boolean -> putBoolean(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Short -> putShort(key, value)
        is Long -> putLong(key, value)
        is Byte -> putByte(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Float -> putFloat(key, value)
        is Bundle -> putBundle(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        else -> throw IllegalStateException("Type of property $key is not supported")
    }
}